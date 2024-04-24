from mininet.topo import Topo
from mininet.net import Mininet
from mininet.node import Node
import sys
import os
import json
from src.restful_mininet.util.log import *
from time import sleep

from mininet import log
from os import path
import signal
import shutil
import functools

BIN_DIR = "/usr/lib/frr"
DEBUG = True


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
        super().__init__(name, privateDirs=["/var/run/frr", "/etc/frr"], **params)
        self.daemon_dict = {}
        self.log_path = None
        if path.exists("/etc/frr"):
            shutil.rmtree("/etc/frr")
        assert (not path.exists("/etc/frr"))

        os.makedirs("/etc/frr")
        assert (path.exists("/etc/frr"))
        assert (len(os.listdir("/etc/frr")) == 0)

        self.cmds_error(["touch", "/etc/frr/vtysh.conf"])
        self.cmd('''echo "service integrated-vtysh-config" >> /etc/frr/vtysh.conf''')
        assert (len(os.listdir("/etc/frr")) != 0)

    def load_frr(self, daemons, conf_dir, universe=False):
        self.log_path = path.join("/home/frr/log", self.name)
        for daemon in daemons:
            self._load_daemon(daemon, conf_dir, universe)
        if (universe):
            conf_path = path.join(conf_dir, f"{self.name}.conf")
            self.cmds_error(["cp", conf_path, "/etc/frr/frr.conf"])
            try:
                self.cmds_error(["vtysh", "-b"])
            except:
                pass
            infoaln("ls /etc/frr", self.cmds(["ls", "/etc/frr"]))
        if DEBUG:
            self.log_load_frr()

    def log_load_frr(self):
        infoaln("daemon_dict", self.daemon_dict)
        infoaln("zebra_log", self.cmds(["cat", self.daemon_dict["zebra"]["log_path"]]))
        infoaln("ospf_log", self.cmds(["cat", self.daemon_dict["ospfd"]["log_path"]]))
        infoaln("ls log/route", self.cmds(["ls", self.log_path]))
        infoaln("cat /etc/frr/vtysh.conf", self.cmds(["cat", "/etc/frr/vtysh.conf"]))
        infoaln("ls run", self.cmds(["ls", "/run/frr"]))

    def _load_daemon(self, daemon_name, work_dir: str, universe=False):
        # if path.exists(self.log_path):
        #     shutil.rmtree(self.log_path)
        # assert (not path.exists(self.log_path))

        os.makedirs(self.log_path, exist_ok=True)
        assert (path.exists(self.log_path))
        #assert (len(os.listdir(self.log_path)) == 0)
        pid_path = path.join(self.log_path, f"{self.name}_{daemon_name}.pid")
        log_path = path.join(self.log_path, f"{self.name}_{daemon_name}.log")
        conf_path = path.join(work_dir, f"{self.name}_{daemon_name}.conf")
        self.daemon_dict[daemon_name] = {"pid_path": pid_path, "log_path": log_path, "conf_path": conf_path}
        if (not universe):
            self.cmds(
            [f"{BIN_DIR}/{daemon_name}", "-u", "root", "-f", conf_path, "-d", "-i", pid_path, "--log-level", "debug",
             "--log", f"file:{log_path}"])
        else:
             self.cmds(
            [f"{BIN_DIR}/{daemon_name}", "-u", "root", "-d", "-i", pid_path, "--log-level", "debug",
             "--log", f"file:{log_path}"])
        with open(pid_path, "r") as file:
            daemon_pid = int(file.read())
            self.daemon_dict[daemon_name]["daemon_pid"] = daemon_pid

    def stop_frr(self):
        for v in self.daemon_dict.values():
            kill_pid(v["daemon_pid"])
        if self.log_path != None:
            self.cmds_error(["cp", "-r", "/run/frr", path.join(self.log_path, "run")])
        log.info("cleaned\n")

    def terminate(self):
        self.stop_frr()
        super().terminate()

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
        return self.cmds(cmds_list)
    def daemon_cmds(self, cmds:list, daemon_name=None):
        cmds_list = ["vtysh"]
        if daemon_name is not None:
            cmds_list = cmds_list + ["-d", daemon_name]
        cmds_list = cmds_list + [f'-c "{cmd}"' for cmd in cmds]
        return self.cmds(cmds_list)
    
    def dump_info_to_json(self):
        j = dict()
        router = self.name
        j[router] = dict()
        j[router]["ospf-daemon"] = json.loads(self.daemon_cmds(["show ip ospf json"]))
        j[router]["intfs"] = json.loads(self.daemon_cmds(["show interface json"]))
        j[router]["ospf-intfs"] = json.loads(self.daemon_cmds(["show ip ospf interface json"]))
        j[router]['neighbors'] = json.loads(self.daemon_cmds(["show ip ospf neighbor json"]))
        j[router]['routing-table'] = json.loads(self.daemon_cmds(['show ip ospf route json']))
        return json.dumps(j, indent=4)
    
    def dump_info(self):
        j = {}
        #print("=======")
        #print(self.daemon_cmds(["show ip ospf json"]))
        #print("=======")
        st = self.daemon_cmds(["show ip ospf json"])
        if st == "":
            j["ospf-daemon"] = {}
        else:
            j["ospf-daemon"] = json.loads(self.daemon_cmds(["show ip ospf json"]))
        j["intfs"] = json.loads(self.daemon_cmds(["show interface json"]))
        j["ospf-intfs"] = json.loads(self.daemon_cmds(["show ip ospf interface json"]))
        j['neighbors'] = json.loads(self.daemon_cmds(["show ip ospf neighbor json"]))
        j['routing-table'] = json.loads(self.daemon_cmds(['show ip ospf route json']))
        j['running-config'] = self.daemon_cmds(["show running-config"])
        return j

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
