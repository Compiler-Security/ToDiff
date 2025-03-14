from mininet.topo import Topo
from mininet.net import Mininet
from mininet.node import Node
import sys
import os
import json
from src.restful_mininet.util.log import *
from time import sleep
import traceback
from mininet import log
from os import path
import signal
import shutil
import functools

BIN_DIR = "/usr/lib/frr"
DEBUG = False


def halt():
    os.kill(os.getpid(), signal.CTRL_C_EVENT)


def kill_pid(pid: int):
    try:
        os.kill(pid, 15)
    except ProcessLookupError:
        log.warn(f"pid {pid} not alive")
    except Exception as e:
        log.error(f"pid {pid} can't be killed")


class FrrNode(Node):
    def __init__(self, name, inNamespace=True, **params):
        super().__init__(name, privateDirs=["/run/frr", "/etc/frr"], **params)
        self.daemon_dict = {}
        self.log_path = None
        self.daemons = []
        if path.exists("/etc/frr"):
            shutil.rmtree("/etc/frr")
        assert (not path.exists("/etc/frr"))

        os.makedirs("/etc/frr")
        assert (path.exists("/etc/frr"))
        assert (len(os.listdir("/etc/frr")) == 0)

        self.cmds_error(["touch", "/etc/frr/vtysh.conf"])
        self.cmd('''echo "service integrated-vtysh-config" >> /etc/frr/vtysh.conf''')
        self.cmd("ulimit -n 512")
        assert (len(os.listdir("/etc/frr")) != 0)

        self.raw_conf_check = None

    #################### util ########################################
    def cmds(self, cmds, verbose=False):
        cmd_str = " ".join(cmds)
        res = self.cmd(cmd_str, verbose=verbose, printPid=True)
        return res

    def cmds_error(self, cmds, verbose=False):
        res, e, c = self.pexec(cmds)
        if c != 0:
            raise Exception((cmds, e))
        return res
    

    def daemon_cmd(self, cmd:str, daemon_name=None):
        cmds_list = ["vtysh"]
        if daemon_name is not None:
            cmds_list = cmds_list + ["-d", daemon_name]
        cmds_list = cmds_list + [f'-c "{cmd}"']
        res = self.cmds(cmds_list)
        return res
    
    def daemon_cmds(self, cmds:list, daemon_name=None):
        cmds_list = ["vtysh"]
        if daemon_name is not None:
            cmds_list = cmds_list + ["-d", daemon_name]
        cmds_list = cmds_list + [f'-c "{cmd}"' for cmd in cmds]
        res =  self.cmds(cmds_list)
        return res
    

    ####################### load/stop ########################
    #-----------------------helpers---------------------------
    #testname/conf/r1.conf
    def _getConfPath(self):
        return path.join(self.conf_dir, f"{self.name}.conf")
    
    def _load_frr_conf(self):
        conf_path = self._getConfPath()
        self.cmds_error(["cp", conf_path, "/etc/frr/frr.conf"])
        if self.raw_conf_check != None:
            with open("/etc/frr/frr.conf", "r") as fp:
                raw_conf_now = fp.read()
            assert raw_conf_now == self.raw_conf_check.replace("\r\n", "\n"), "new conf not right!"
        try:
            self.cmds_error(["vtysh", "-b"])
        except:
            #FIXME
            pass
    
    def _save_frr_conf(self):
        warnaln(f"+save frr conf ", self.name)
        raw_conf = self.daemon_cmds(["show running-config"])
        raw_conf = raw_conf[raw_conf.find("!"):]
        self.raw_conf_check = raw_conf
        with open(path.join(self._getConfPath()), "w") as fp:
            fp.write(raw_conf)
        warnaln(f"-save frr conf ", self.name)

    def _load_daemon(self, daemon_name, work_dir: str, universe=False):
        #if daemon is already running, we don't need to run it again
        if daemon_name in self.daemon_dict:
            return
        warnaln(f"  + load daemon {daemon_name}", "")
        pid_path = path.join(self.log_path, f"{self.name}_{daemon_name}.pid")
        log_path = path.join(self.log_path, f"{self.name}_{daemon_name}.log")
        conf_path = path.join(work_dir, f"{self.name}_{daemon_name}.conf")
        self.daemon_dict[daemon_name] = {"pid_path": pid_path, "log_path": log_path, "conf_path": conf_path}
        while True:
            if (not universe):
                assert False, 'halt and graceful shutdown must use universal config'
                self.cmds(
                [f"{BIN_DIR}/{daemon_name}", "-u", "root", "-f", conf_path, "-d", "-i", pid_path, "--log-level", "debug",
                    "--log", f"file:{log_path}"])
            else:
                #we set asan_logs to /var/run/frr/{daemon_name}.asan
                ress = self.cmds(
                [f"export ASAN_OPTIONS=log_path=/run/frr/{daemon_name}.asan && ", f"{BIN_DIR}/{daemon_name}", "--limit-fds", "64", "-u", "root", "-d", "-i", pid_path, "--log-level", "debug",
                "--log", f"file:{log_path}"])
                # ress = self.cmds(
                # [f"{BIN_DIR}/{daemon_name}", "--limit-fds", "64", "-u", "root", "-d", "-i", pid_path, "--log-level", "debug",
                # "--log", f"file:{log_path}"])
                if(path.exists(pid_path)):
                    break
                else:
                    sleep(1)
                    warnaln(f"      load daemon {daemon_name} res error, try again...\n", ress[:ress.find("\n")]) 
                    
        with open(pid_path, "r") as file:
            daemon_pid = int(file.read())
            self.daemon_dict[daemon_name]["daemon_pid"] = daemon_pid
        warnaln(f"  - load daemon {daemon_name}", "")
    
    #load frr will load all daemons in daemons
    #if test_daemon is stopped, it will first load backup conf via vtysh
    def _load_frr(self, daemons, test_daemon, conf_dir, log_function, universe=False):
        self.conf_dir = conf_dir
        self.log_path = path.join(path.dirname(conf_dir), "log", self.name)
        if len(self.daemon_dict.keys()) == 0:
            if path.exists(self.log_path):
                shutil.rmtree(self.log_path)
            assert (not path.exists(self.log_path))
            os.makedirs(self.log_path)
        assert (path.exists(self.log_path))
        self.daemons = daemons
        test_daemon_running = test_daemon in self.daemon_dict
        for daemon in daemons:
            self._load_daemon(daemon, conf_dir, universe)
        if (universe and not test_daemon_running):
            self._load_frr_conf()
            #erroraln("cat /etc/frr/frr.conf", self.cmds(["cat", "/etc/frr/frr.conf"]))
        if DEBUG == True:
            log_function()
    
    #------------------ LOAD TEST DAEMON--------------------------
    #MULTI:
    #conf_dir testname/conf
    def load_ospf(self, daemons, conf_dir, universe=False):
        self._load_frr(daemons, "ospfd", conf_dir, self._log_load_ospf, universe)

    def load_isis(self, daemons, conf_dir, universe=False):
        self._load_frr(daemons, "isisd", conf_dir, self._log_load_isis, universe)

    def load_rip(self, daemons, conf_dir, universe=False):
        self._load_frr(daemons, "ripd", conf_dir, self._log_load_isis, universe)

    def load_babel(self, daemons, conf_dir, universe=False):
        self._load_frr(daemons, "babeld", conf_dir, self._log_load_isis, universe)
    #------------------ STOP TEST DAEMON--------------------------
    def stop_ospfd(self, conf_dir):
        if "ospfd" in self.daemon_dict:
            self._save_frr_conf()
            kill_pid(self.daemon_dict["ospfd"]["daemon_pid"])
            os.remove(self.daemon_dict["ospfd"]["pid_path"])
            del self.daemon_dict["ospfd"]

    def stop_isisd(self, conf_dir):
        if "isisd" in self.daemon_dict:
            self._save_frr_conf()
            kill_pid(self.daemon_dict["isisd"]["daemon_pid"])
            os.remove(self.daemon_dict["isisd"]["pid_path"])
            del self.daemon_dict["isisd"]
    
    def stop_ripd(self, conf_dir):
        if "isisd" in self.daemon_dict:
            self._save_frr_conf()
            kill_pid(self.daemon_dict["ripd"]["daemon_pid"])
            os.remove(self.daemon_dict["ripd"]["pid_path"])
            del self.daemon_dict["ripd"]

    def stop_babeld(self, conf_dir):
        if "isisd" in self.daemon_dict:
            self._save_frr_conf()
            kill_pid(self.daemon_dict["babeld"]["daemon_pid"])
            os.remove(self.daemon_dict["babeld"]["pid_path"])
            del self.daemon_dict["babeld"]

    def stop_frr(self):
        #MULTI:
        #FXIME we should add all the test_daemon to stop_daemons
        stop_daemons = [self.stop_ospfd, self.stop_isisd, self.stop_ripd, self.stop_babeld]
        for stop_daemon in stop_daemons:
            stop_daemon(self.conf_dir)

        self.cmds_error(["cp", "-r", "/run/frr", path.join(self.log_path, "run")])
        for v in self.daemon_dict.values():
            kill_pid(v["daemon_pid"])
        self.daemon_dict = {}
        log.info("cleaned\n")

    def terminate(self):
        erroraln("+ stop router ", self.name)
        self.stop_frr()
        super().terminate()
        erroraln("- stop router ", self.name)


    def check_asan(self):
        warnln("+ check asan, TODO", "")
    #     #for d in os.listdir("/run/frr"):        #    if ()
    
    #-------------------LOG Load TEST DAEMON---------------------------
    def _log_load_ospf(self):
        infoaln("daemon_dict", self.daemon_dict)
        infoaln("zebra_log", self.cmds(["cat", self.daemon_dict["zebra"]["log_path"]]))
        infoaln("ospf_log", self.cmds(["cat", self.daemon_dict["ospfd"]["log_path"]]))
        infoaln("ls log/route", self.cmds(["ls", self.log_path]))
        infoaln("cat /etc/frr/vtysh.conf", self.cmds(["cat", "/etc/frr/vtysh.conf"]))
        infoaln("ls run", self.cmds(["ls", "/run/frr"]))
    
    def _log_load_isis(self):
        infoaln("daemon_dict", self.daemon_dict)
        infoaln("zebra_log", self.cmds(["cat", self.daemon_dict["zebra"]["log_path"]]))
        infoaln("isis_log", self.cmds(["cat", self.daemon_dict["isisd"]["log_path"]]))
        infoaln("ls log/route", self.cmds(["ls", self.log_path]))
        infoaln("cat /etc/frr/vtysh.conf", self.cmds(["cat", "/etc/frr/vtysh.conf"]))
        infoaln("ls run", self.cmds(["ls", "/run/frr"]))
    
    def _log_load_rip(self):
        infoaln("daemon_dict", self.daemon_dict)
        infoaln("zebra_log", self.cmds(["cat", self.daemon_dict["zebra"]["log_path"]]))
        infoaln("isis_log", self.cmds(["cat", self.daemon_dict["ripd"]["log_path"]]))
        infoaln("ls log/route", self.cmds(["ls", self.log_path]))
        infoaln("cat /etc/frr/vtysh.conf", self.cmds(["cat", "/etc/frr/vtysh.conf"]))
        infoaln("ls run", self.cmds(["ls", "/run/frr"]))
    ################################### DUMP #################################
    #---------------------------------helper----------------------------------
    def _collect_info_ospf(self, j, item, cmds, isjson):
        warnaln(f"  + collect {item}", "")
        if isjson == True:
            info = self.daemon_cmds([cmds])
            if (item == "ospf-daemon" and info == ""):
                #we should handle special case of "ospf-daemon"
                j[item] = json.loads("{}")
            else:
                j[item] = json.loads(self.daemon_cmds([cmds]))
        else:
            j[item] = self.daemon_cmds([cmds])
        warnaln(f"  - collect {item}", "")
    
    
    def _collect_info_isis(self, j, item, cmds, isjson):
        warnaln(f"  + collect {item}", "")
        if isjson == True:
            info = self.daemon_cmds([cmds])
            if (item == "isis-daemon" and info == ""):
                #we should handle special case of "isis-daemon"
                j[item] = json.loads("{}")
            else:
                j[item] = json.loads(self.daemon_cmds([cmds]))
        else:
            j[item] = self.daemon_cmds([cmds])
        warnaln(f"  - collect {item}", "") 

    #------------------------OSPF-------------------------------------
    def dump_info_ospf(self):
        j = {}
        warnaln(f"+ collect {self.name}", "")
        if "ospfd" not in self.daemon_dict:
            j["ospf-up"] = False
        else:
            j["ospf-up"] = True
            self._collect_info_ospf(j, "ospf-daemon", "show ip ospf json", True)
            self._collect_info_ospf(j, "ospf-intfs", "show ip ospf interface json", True)
            self._collect_info_ospf(j, "neighbors", "show ip ospf neighbor json", True)
            self._collect_info_ospf(j, "routing-table", "show ip ospf route json", True)
            self._collect_info_ospf(j, "database", "show ip ospf database detail json", True)
        if "zebra" in self.daemon_dict:
            self._collect_info_ospf(j, "running-config", "show running-config", False)
            self._collect_info_ospf(j, "intfs", "show interface json", True)
        warnaln(f"- collect {self.name}", "")
        #warnaln("end dump ospf json", "")
        return j
    
    def dump_ospf_neighbor_info(self):
        info = self.daemon_cmds(["show ip ospf neighbor json"])
        try:
            return json.loads(info)
        except Exception as e:
            traceback.print_exception(e)
            return None
    
    def dump_ospf_intfs_info(self):
        info = self.daemon_cmds(["show ip ospf interface json"])
        try:
            return json.loads(info)
        except Exception as e:
            traceback.print_exception(e)
            return None
        
    #---------------------------------ISIS---------------------------------
    def dump_info_isis(self):
        j = {}
        warnaln(f"+ collect {self.name}", "")
        if "isisd" not in self.daemon_dict:
            j["isis-up"] = False
        else:
            j["isis-up"] = True
            self._collect_info_isis(j, "isis-daemon", "show isis summary json", True)
            self._collect_info_isis(j, "isis-intfs", "show isis interface detail json", True)
            self._collect_info_isis(j, "neighbors", "show isis neighbor detail json", True)
            self._collect_info_isis(j, "routing-table", "show isis route json", True)
        if "zebra" in self.daemon_dict:
            self._collect_info_isis(j, "running-config", "show running-config", False)
            self._collect_info_isis(j, "intfs", "show interface json", True)
        warnaln(f"- collect {self.name}", "")
        #warnaln("end dump ospf json", "")
        return j
    
    def dump_isis_database(self):
        info = self.daemon_cmds(["show isis database detail"])
        return info

    def dump_isis_intfs_info(self):
        info = self.daemon_cmds(["show isis interface detail json"])
        try:
            return json.loads(info)
        except Exception as e:
            traceback.print_exception(e)
            return None

    def dump_isis_daemon_info(self):
        info = self.daemon_cmds(["show isis summary json"])
        try:
            return json.loads(info)
        except Exception as e:
            traceback.print_exception(e)
            return None
        
    def dump_route_info(self):
        info = self.daemon_cmds(["show ip route"])
        return info
    
    def dump_isis_route_info(self):
        info = self.daemon_cmds(["show isis route "])
        return info
    def dump_isis_neighbor_info(self):
        info = self.daemon_cmds(["show isis neighbor detail json"])
        try:
            return json.loads(info)
        except Exception as e:
            traceback.print_exception(e)
            return None
    #---------------------------------RIP---------------------------------
    def dump_info_rip(self):
        j = {}
        warnaln(f"+ collect {self.name}", "")
        if "ripd" not in self.daemon_dict:
            j["rip-up"] = False
        else:
            j["rip-up"] = True
            self._collect_info_ospf(j, "rip-route", "show ip rip", False)
            self._collect_info_ospf(j, "status", "show ip rip status", False)
            self._collect_info_ospf(j, "routing-table", "show ip route json", True)
        if "zebra" in self.daemon_dict:
            self._collect_info_ospf(j, "running-config", "show running-config", False)
            #self._collect_info_ospf(j, "intfs", "show interface json", True)
        warnaln(f"- collect {self.name}", "")
        #warnaln("end dump ospf json", "")
        return j

    def dump_info_babel(self):
        j = {}
        warnaln(f"+ collect {self.name}", "")
        if "babeld" not in self.daemon_dict:
            j["babel-up"] = False
        else:
            j["babel-up"] = True
            self._collect_info_ospf(j, "babel-route", "show babel route", False)
            self._collect_info_ospf(j, "babel-interface", "show babel interface", False)
            self._collect_info_ospf(j, "babel-neighbor", "show babel neighbor", False)
            self._collect_info_ospf(j, "babel-parameters", "show babel parameters", False)
            #self._collect_info_ospf(j, "routing-table", "show ip route json", True)
        if "zebra" in self.daemon_dict:
            self._collect_info_ospf(j, "running-config", "show running-config", False)
            #self._collect_info_ospf(j, "intfs", "show interface json", True)
        warnaln(f"- collect {self.name}", "")
        #warnaln("end dump ospf json", "")
        return j
    #MULTI:

if __name__ == "__main__":
    setLogLevel('info')
    WORK_DIR = path.join(path.dirname(path.dirname(path.dirname(path.dirname(path.abspath(__file__))))), "test",
                         "simple", "frr_conf")
    assert (WORK_DIR == "/home/frr/topo-fuzz/test/simple/frr_conf"), WORK_DIR
    topo = Topo()
    #frr = functools.partial(FrrNode, privateDirs=["/var/run/frr"])
    r1 = topo.addHost("r1", cls=FrrNode)
    r2 = topo.addHost("r2", cls=FrrNode)
    r3 = topo.addHost("r3", cls=FrrNode)
    topo.addLink(r1, r2)
    topo.addLink(r2, r3)
    # build network
    net = Mininet(topo)
    try:
        net.start()
        net.nameToNode["r1"].load_frr(daemons=["zebra", "ospfd"], conf_dir=WORK_DIR)
        net.nameToNode["r2"].load_frr(daemons=["zebra", "ospfd"], conf_dir=WORK_DIR)
        net.nameToNode["r3"].load_frr(daemons=["zebra", "ospfd"], conf_dir=WORK_DIR)
        sleep(1)
        # print(net.nameToNode["r1"].cmd("cat /tmp/r1-ospfd.log"))
        # sleep(20)
        infoaln("r1 ospf route", net.nameToNode["r1"].daemon_cmd("show ip ospf interface"))
        # print(net.nameToNode["r1"].cmd("cat /tmp/r1-ospfd.log"))
        # net.delLinkBetween(net.nameToNode["r1"],  net.nameToNode["r2"])
        # sleep(15)
        # net.nameToNode["r1"].daemon_multicmd(["show ip ospf route"])
        net.stop()
        # while(1):
        #     pass
    except BaseException as e:
        error(f"\033[31merror\033[0m [{e}]\n")
        net.stop()
