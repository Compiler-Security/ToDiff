from mininet.topo import Topo
from mininet.net import Mininet
from mininet.node import  Node
from mininet.log import setLogLevel
from time import sleep
import os
from mininet import log
from os import path
import signal
import shutil
from mininet.cli import CLI

BIN_DIR="/usr/lib/frr"
WORK_DIR = path.join(path.dirname(path.abspath(__file__)), "frr_conf")

def exit():
    os.kill(os.getpid(), signal.CTRL_C_EVENT)

def kill_pid(pid:int):
    try:
        os.kill(pid, 15)
    except ProcessLookupError:
        log.warn(f"pid {pid} not alive")
    except Exception as e:
        log.error(f"pid {pid} can't be killed")


class FrrNode(Node):
    def load_frr(self, daemons, conf_dir):
        mkdir_cmds = ["mkdir", "-p", f"/home/frr/vtysh"]
        self.cmd_frr_error(mkdir_cmds)
        self.vtysh_dir = f"/home/frr/vtysh/{self.name}"
        #mkdir_cmds = ["mkdir", "-p", self.vtysh_dir]
        #FIXME: ospf.db zebra.db zserv.api is not handle correctly

        #self.cmd(mkdir_cmds)
        self.daemon_dict = {}
        for daemon in daemons:
            self._load_daemon(daemon, conf_dir)

    def _load_daemon(self, daemon_name, work_dir: str):
        pid_path = path.join("/tmp", f"{self.name}-{daemon_name}.pid")
        log_path = path.join("/tmp", f"{self.name}-{daemon_name}.log")
        conf_path = path.join(work_dir, f"{self.name}.conf")
        self.daemon_dict[daemon_name] = {"pid_path":pid_path, "log_path":log_path, "conf_path":conf_path}
        self.cmd_error([f"{BIN_DIR}/{daemon_name}", "-u", "root", "-f", conf_path, "-d", "-i", pid_path, "--log-level", "debug", "--log", f"file:{log_path}"])
        with open(pid_path, "r") as file:
            daemon_pid = int(file.read())
            self.daemon_dict[daemon_name]["daemon_pid"] = daemon_pid

    def terminate( self ):
        for v in self.daemon_dict.values():
            kill_pid(v["daemon_pid"])
            os.remove(v["pid_path"])
        if path.exists(self.vtysh_dir):
            shutil.rmtree(self.vtysh_dir)
        log.info("cleaned\n")
        super().terminate()

    def cmd_frr_error(self, cmds):
        cmds = ["sudo", "-u", "frr"] + cmds
        self.cmd_error(cmds)

    def cmd_frr_frrvty_error(self, cmds):
        cmds = ["sudo", "-u", "frr", "-g", "frr"] + cmds
        self.cmd_error(cmds)
    def cmd_error(self, cmds):
        try:
            cmd_str = " ".join(cmds)
            res = self.cmd(cmd_str, verbose=True, printPid=True)
        except Exception as e:
            log.error(f"error: {e}")
            exit()

    def daemon_multicmd(self,  cmds, daemon_name = None):
        cmds_list = ["vtysh"]
        if daemon_name is not None:
            cmds_list = cmds_list + ["-d", daemon_name]
        cmds_list = cmds_list + [f'-c "{cmd}"' for cmd in cmds]
        return self.cmd_error(cmds_list)

import functools
def simpleTest():
    topo = Topo()
    frr = functools.partial(FrrNode, privateDirs = ["/var/run/frr"])
    r1 = topo.addHost("r1", cls=frr)
    r2 = topo.addHost("r2", cls=frr)
    r3 = topo.addHost("r3", cls=frr)
    topo.addLink(r1, r2)
    topo.addLink(r2, r3)
    #build network
    net = Mininet(topo)
    net.nameToNode["r1"].load_frr(daemons=["zebra", "ospfd"], conf_dir=WORK_DIR)
    net.nameToNode["r2"].load_frr(daemons=["zebra", "ospfd"], conf_dir=WORK_DIR)
    net.nameToNode["r3"].load_frr(daemons=["zebra", "ospfd"], conf_dir=WORK_DIR)
    try:
        net.start()
        sleep(1)
        print(net.nameToNode["r1"].cmd("cat /tmp/r1-ospfd.log"))
        sleep(20)
        net.nameToNode["r1"].daemon_cmds(["show ip ospf route"])
        print(net.nameToNode["r1"].cmd("cat /tmp/r1-ospfd.log"))
        net.delLinkBetween(net.nameToNode["r1"],  net.nameToNode["r2"])
        sleep(15)
        net.nameToNode["r1"].daemon_cmds(["show ip ospf route"])
        net.stop()
        # while(1):
        #     pass
    except BaseException as e:
        log.error(f"{e}\n")
        net.stop()


if __name__ == "__main__":
    setLogLevel('info')
    simpleTest()
