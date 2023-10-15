import logging

from mininet.node import Node
from util import BIN_DIR
from os import path


class Router(Node):
    def load_daemon(self, daemon_name, work_dir: str):
        pid_path = path.join(work_dir, f"{self.name}-{daemon_name}.pid")
        log_path = path.join(work_dir, f"{self.name}-{daemon_name}.log")
        conf_path = path.join(work_dir, f"{self.name}.conf")
        cmd_str = f"{BIN_DIR}/{daemon_name}"
        f" -f {conf_path}"
        " -d"
        f" -i {pid_path}"
        f" > {log_path} 2>&1"
        logging.info(f"load daemon command {cmd_str}")
        try:
            self.cmd(cmd_str)
        except Exception as e:
            logging.error(f"{e}")
            exit(-1)
    def daemon_multicmd(self, daemon_name, cmds):
        vtysh_path = path.join(BIN_DIR, "vtysh")


