from mininet.topo import Topo
from mininet.net import Mininet
from mininet.node import  Node
from mininet.log import setLogLevel
from time import sleep
import os
from mininet import log
from os import path
import signal

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
    def __init__(self, name, **params):
        #daemons:list, work_dir: str
        super().__init__(name, **params)
        self.daemon_dict = {}
        if "daemons" not in params or "work_dir" not in params:
            exit()
        for daemon in params["daemons"]:
            self.load_daemon(daemon, params["work_dir"])

    def load_daemon(self, daemon_name, work_dir: str):
        pid_path = path.join("/tmp", f"{self.name}-{daemon_name}.pid")
        log_path = path.join("/tmp", f"{self.name}-{daemon_name}.log")
        conf_path = path.join(work_dir, f"{self.name}.conf")
        self.daemon_dict[daemon_name] = {"pid_path":pid_path, "log_path":log_path, "conf_path":conf_path}
        cmd_str = f"{BIN_DIR}/{daemon_name}" \
        f" -f {conf_path}" \
        " -d" \
        f" -i {pid_path}" \
        f" > {log_path} 2>&1"
        #log.info(f"load daemon command {cmd_str}\n")
        self.cmd_error(cmd_str)
        with open(pid_path, "r") as file:
            daemon_pid = int(file.read())
            self.daemon_dict[daemon_name]["daemon_pid"] = daemon_pid

    def terminate( self ):
        for v in self.daemon_dict.values():
            kill_pid(v["daemon_pid"])
            os.remove(v["pid_path"])
        super().terminate()

    def cmd_error(self, cmd_str, *args, **kwargs):
        try:
            #log.info(f"cmd: {cmd_str}\n")
            res = self.cmd(cmd_str, verbose=True, printPid=True)
            #log.warn(f"cmd res: {res}\n")
        except Exception as e:
            log.error(f"error: {e}")
            exit()

    def daemon_multicmd(self, daemon_name, cmds):
        # cmds_str = " ".join([f'-c "{cmd}"' for cmd in cmds])
        cmds_list = []
        for cmd in cmds:
            cmds_list.append("-c")
            cmds_list.append(cmd)
        res = self.pexec(["vtysh", "-d", daemon_name])
        _ = res



class FrrTopo(Topo):
    def build(self, n=2):
        switch = self.addSwitch('s1')
        for h in range(n):
            r_name = f"r{h + 1}"
            host = self.addHost(r_name, cls=FrrNode, daemons=["zebra", "ospfd"], work_dir=WORK_DIR)
            self.addLink(host, switch)






def simpleTest():
    topo = FrrTopo(n=2)
    net = Mininet(topo)
    try:
        net.start()
        r1, r2 = net.hosts[0], net.hosts[1]
        r1.daemon_multicmd("ospfd", ["show ip ospf"])
        while(1):
            pass
    except:
        net.stop()


if __name__ == "__main__":
    setLogLevel('info')
    simpleTest()

# from mininet.topolib import LinearTopo
# tree4 = TreeTopo(depth=2,fanout=2)
# net = Mininet(topo=tree4)
# net.start()
# h1, h4  = net.hosts[0], net.hosts[3]
# h1.cmd('sudo /home/frr/frr/ospfd/ospfd -f /home/mininet/frr.conf -i /tmp/frr_h1.pid > /tmp/h1.out 2>&1')
# h4.cmd('sudo /home/frr/frr/ospfd/ospfd -f /home/mininet/frr.conf -i /tmp/frr_h2.pid > /tmp/h2.out 2>&1')
# net.stop()