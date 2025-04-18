import sys
import traceback
from os import path
path_to_add = path.dirname(path.dirname(path.dirname(path.dirname(path.abspath(__file__)))))
if path_to_add not in sys.path:
    sys.path.append(path_to_add)

from src.restful_mininet.net import testnet
from src.restful_mininet.exec.inst import MininetInst
from src.restful_mininet.util.log import *
from mininet.cli import CLI
import os
import json
import time
import re

class executor:
    def __init__(self):
        pass
    def __init__(self, conf_path, output_dir_str, minWaitTime, maxWaitTime, protocol) -> None:
        setLogLevel('info')
        self.conf_path = conf_path
        self.maxWaitTime = maxWaitTime
        self.minWaitTime = minWaitTime
        with open(self.conf_path) as fp:
             self.conf = json.load(fp)
        self.step_nums = self.conf['step_nums']
        self.conf_name = self.conf['conf_name']
        self.round_num = self.conf['round_num']
        self.output_dir = path.join(output_dir_str, self.conf_name)
        self.routers = self.conf['routers']
        self.protocol = protocol
        os.makedirs(self.output_dir, exist_ok=True)
        ###attention conf_dir is used to Temporarily store the configuration before loading /etc/frr.conf. When ospf is closed, memory will be written and the result of the write will be copied here.
        self.conf_file_dir = path.join(self.output_dir, 'conf')
        os.makedirs(self.conf_file_dir, exist_ok=True)
        os.system("mn -c 2> /dev/null")
#==============INIT CONF====================
    def _init_ospf(self, router_name, ospf_commands):
        conf_name = f"{router_name}.conf"
        with open(path.join(self.conf_file_dir, conf_name), 'w') as fp:
            for opa in ospf_commands:
                for op in opa.split(";"):
                    fp.write(op)
                    fp.write('\n')

    def _init_isis(self, router_name, isis_commands):
        conf_name = f"{router_name}.conf"
        with open(path.join(self.conf_file_dir, conf_name), 'w') as fp:
            for opa in isis_commands:
                for op in opa.split(";"):
                    fp.write(op)
                    fp.write('\n')

    def _init_rip(self, router_name, isis_commands):
        conf_name = f"{router_name}.conf"
        with open(path.join(self.conf_file_dir, conf_name), 'w') as fp:
            for opa in isis_commands:
                for op in opa.split(";"):
                    fp.write(op)
                    fp.write('\n')

    def _init_babel(self, router_name, isis_commands):
        conf_name = f"{router_name}.conf"
        with open(path.join(self.conf_file_dir, conf_name), 'w') as fp:
            for opa in isis_commands:
                for op in opa.split(";"):
                    fp.write(op)
                    fp.write('\n')

    def _init_openfabric(self, router_name, openfabric_commands):
        conf_name = f"{router_name}.conf"
        with open(path.join(self.conf_file_dir, conf_name), 'w') as fp:
            for opa in openfabric_commands:
                for op in opa.split(";"):
                    fp.write(op)
                    fp.write('\n')
#===============RUN COMMANDS=================
    def _run_phy_commands(self, net, ctx, phy_commands):
        res = []
        for op in phy_commands:
            ress = MininetInst(op, net, self.conf_file_dir, ctx).run()
            res.append(ress)
            if (ress != 0):
                erroraln(f"phy exec <{op}> wrong! exit the test, reason is:\n", ress)
                assert False
        warnaln("   PHY commands result:", res)
        return res
    
    def _run_ospf_commands(self, net:testnet.TestNet, router_name, ospf_commands):
        res = []
        for op in ospf_commands:
            if op in ["clear ip ospf process", "clear ip ospf neighbor", "clear ip ospf interface", "write terminal"]:
                res.append(net.run_frr_cmds(router_name, [op]))
            else:
                resStr = ""
                sub_ops = op.split(";")
                ctx_op = sub_ops[0]
                #single ctx_op eg. router ospf
                if (len(sub_ops) == 1):
                    res.append(net.run_frr_cmds( router_name, ['configure terminal', ctx_op]))
                else:
                    sub_ops = sub_ops[1:]
                    for sub_op in sub_ops:
                        r = net.run_frr_cmds(router_name, ['configure terminal', ctx_op, sub_op])
                        if r != "":
                            resStr += sub_op + "<-" + r + ";"
                    res.append(resStr)
        warnaln("   OSPF commands result:", res)
        return res
    
    def _run_isis_commands(self, net:testnet.TestNet, router_name, isis_commands):
        res = []
        for op in isis_commands:
            if op in ["write terminal"]:
                res.append(net.run_frr_cmds(router_name, [op]))
            else:
                resStr = ""
                sub_ops = op.split(";")
                ctx_op = sub_ops[0]
                #single ctx_op eg. router ospf
                if (len(sub_ops) == 1):
                    res.append(net.run_frr_cmds( router_name, ['configure terminal', ctx_op]))
                else:
                    sub_ops = sub_ops[1:]
                    for sub_op in sub_ops:
                        r = net.run_frr_cmds(router_name, ['configure terminal', ctx_op, sub_op])
                        if r != "":
                            resStr += sub_op + "<-" + r + ";"
                    res.append(resStr)
        warnaln("   ISIS commands result:", res)
        return res

    def _run_rip_commands(self, net:testnet.TestNet, router_name, ospf_commands):
        res = []
        for op in ospf_commands:
            resStr = ""
            sub_ops = op.split(";")
            ctx_op = sub_ops[0]
                #single ctx_op eg. router ospf
            if (len(sub_ops) == 1):
                res.append(net.run_frr_cmds( router_name, ['configure terminal', ctx_op]))
            else:
                sub_ops = sub_ops[1:]
                for sub_op in sub_ops:
                    r = net.run_frr_cmds(router_name, ['configure terminal', ctx_op, sub_op])
                    if r != "":
                        resStr += sub_op + "<-" + r + ";"
                res.append(resStr)
        warnaln("   RIP commands result:", res)
        return res

    def _run_babel_commands(self, net:testnet.TestNet, router_name, ospf_commands):
        res = []
        for op in ospf_commands:
            resStr = ""
            sub_ops = op.split(";")
            ctx_op = sub_ops[0]
                #single ctx_op eg. router ospf
            if (len(sub_ops) == 1):
                res.append(net.run_frr_cmds( router_name, ['configure terminal', ctx_op]))
            else:
                sub_ops = sub_ops[1:]
                for sub_op in sub_ops:
                    r = net.run_frr_cmds(router_name, ['configure terminal', ctx_op, sub_op])
                    if r != "":
                        resStr += sub_op + "<-" + r + ";"
                res.append(resStr)
        warnaln("   BABEL commands result:", res)
        return res


    def _run_openfabric_commands(self, net:testnet.TestNet, router_name, openfabric_commands):
        res = []
        for op in openfabric_commands:
            if op in ["write terminal"]:
                res.append(net.run_frr_cmds(router_name, [op]))
            else:
                resStr = ""
                sub_ops = op.split(";")
                ctx_op = sub_ops[0]
                #single ctx_op eg. router openfabric
                if (len(sub_ops) == 1):
                    res.append(net.run_frr_cmds( router_name, ['configure terminal', ctx_op]))
                else:
                    sub_ops = sub_ops[1:]
                    for sub_op in sub_ops:
                        r = net.run_frr_cmds(router_name, ['configure terminal', ctx_op, sub_op])
                        if r != "":
                            resStr += sub_op + "<-" + r + ";"
                    res.append(resStr)
        warnaln("   openfabric commands result:", res)
        return res
#================CHECK CONVERGENCE============
    def _check_converge_ospf(self, net:testnet.TestNet):
        for r_name in self.routers:
            warnln(f"    +check router {r_name}")
            res = net.net.nameToNode[r_name].dump_ospf_neighbor_info()
            if res == None:
                warnln(f"    -check router {r_name} n")
                return False
            for val in res['neighbors'].values():
                for val1 in val:
                    #if neighbor is DR/Backup, converged is full
                    #otherwise converged is 2-way
                    if (val1['converged'] != 'Full' and val1['nbrState'] != '2-Way/DROther'):
                        warnln(f"    -check router {r_name} nb c")
                        return False
                    if (val1['linkStateRetransmissionListCounter'] > 0):
                        warnln(f"    -check router {r_name} nb re")
                        return False
            res = net.net.nameToNode[r_name].dump_ospf_intfs_info()
            for intfName, val in res['interfaces'].items():
                if val['state'] == "Waiting":
                    warnln(f"    -check router {r_name} oi w")
                    warnln(intfName)
                    return False
            warnln(f"    -check router {r_name} y")
        return True
    
    def _check_converge_isis(self, net:testnet.TestNet):
        for r_name in self.routers:
            warnln(f"    +check router {r_name}")
            res = net.net.nameToNode[r_name].dump_isis_daemon_info()
            if res == None:
                warnln(f"    -check router {r_name} n")
                return False

            for vrf in res.get("vrfs", []):
                for area in vrf.get("areas", []):
                    for level in area.get("levels", []):
                        if level.get("spf") != "no pending":
                            warnln(f"    -check router {r_name} daspf")
                            return False
                        # if level.get("lsp-purged") != 0:
                        #     warnln(f"    -check router {r_name} dapurged")
                        #     return False
            res = net.net.nameToNode[r_name].dump_isis_intfs_info()

            for area in res.get("areas", []):
                for circuit in area.get("circuits", []):
                    interface = circuit.get("interface", {})
                    if interface.get("state") == "Initializing":
                        warnln(f"    -check router {r_name} in")
                        return False
            
            running_config = net.net.nameToNode[r_name].dump_isis_running_config()
            match = re.search(r'hostname\s+(\S+)', running_config)
            hostname = match.group(1)

            res = net.net.nameToNode[r_name].dump_isis_neighbor_info()
            for area in res.get("areas", []):
                for circuit in area.get("circuits", []):
                    if circuit.get("adj") == None:
                        continue
                    if circuit.get("adj") != hostname:
                        warnln(f"    -check router {r_name} adj")
                        print(circuit.get("adj"))
                        print(hostname)
                        return False
                    interface = circuit.get("interface", {})
                    if interface.get("state") == "Initializing":
                        warnln(f"    -check router {r_name} nb")
                        return False

            warnln(f"    -check router {r_name} y")

        return True

    def _check_converge_openfabric(self, net:testnet.TestNet):
        for r_name in self.routers:
            warnln(f"    +check router {r_name}")
            res = net.net.nameToNode[r_name].dump_openfabric_daemon_info()
            if res == None:
                warnln(f"    -check router {r_name} n")
                return False

            for vrf in res.get("vrfs", []):
                for area in vrf.get("areas", []):
                    for level in area.get("levels", []):
                        if level.get("spf") != "no pending":
                            warnln(f"    -check router {r_name} daspf")
                            return False
                        # if level.get("lsp-purged") != 0:
                        #     warnln(f"    -check router {r_name} dapurged")
                        #     return False
            res = net.net.nameToNode[r_name].dump_openfabric_intfs_info()

            for area in res.get("areas", []):
                for circuit in area.get("circuits", []):
                    interface = circuit.get("interface", {})
                    if interface.get("state") == "Initializing":
                        warnln(f"    -check router {r_name} in")
                        return False

            running_config = net.net.nameToNode[r_name].dump_openfabric_running_config()
            match = re.search(r'hostname\s+(\S+)', running_config)
            hostname = match.group(1)

            res = net.net.nameToNode[r_name].dump_openfabric_neighbor_info()
            for area in res.get("areas", []):
                for circuit in area.get("circuits", []): 
                    if circuit.get("adj") == None:
                        continue
                    if circuit.get("adj") != hostname:
                        warnln(f"    -check router {r_name} adj")
                        print(circuit.get("adj"))
                        print(hostname)
                        return False    
                    interface = circuit.get("interface", {})
                    if interface.get("state") == "Initializing":
                        warnln(f"    -check router {r_name} nb")
                        return False
            
            warnln(f"    -check router {r_name} y")

        return True
#================RUN PROCESSS==================
    def _run_for_ospf(self, r):
        erroraln(f"\n\n======round{r}======","")
        erroraln("+ mininet init", "")
        net = testnet.TestNet()
        erroraln("- mininet init", "")
        ctx = {"intf":{}}
        commands = self.conf['commands'][r]
        res = []
        for i in range(0, self.step_nums[r]):
            erroraln(f"\n\n>>>> + step{i} <<<<", "")
            
            
            ospf_res = {}
            if i == 0:
                erroraln(f"+ OSPF commands", "")
                for j in range(0, len(self.routers)):
                    router_name = self.routers[j]
                    ospf_ops = commands[i]['ospf'][j]
                    self._init_ospf(router_name, ospf_ops)
                erroraln(f"- OSPF commands", "")
                
                erroraln(f"+ PHY commands", "")
                phy_res = self._run_phy_commands(net, ctx, commands[i]['phy'])
                erroraln(f"- PHY commands", "")
            
            else:
                erroraln(f"+ PHY commands", "")
                phy_res = self._run_phy_commands(net, ctx, commands[i]['phy'])
                erroraln(f"- PHY commands", "")

                erroraln(f"+ OSPF commands", "")
                for j in range(len(self.routers) -1, -1, -1):
                    router_name = self.routers[j]
                    ospf_ops = commands[i]['ospf'][j]
                    tmp = self._run_ospf_commands(net, router_name, ospf_ops)
                    if j == 0:
                        print(tmp)
                    ospf_res[router_name] = tmp
                erroraln(f"- OSPF commands", "")
            
            if i == 0:    
                net.start_net()
            res.append({})
            res[i]['exec'] = {}
            res[i]['exec']['phy'] = phy_res
            res[i]['exec']['ospf'] = ospf_res
            
            sleep_time = commands[i]['waitTime']
            erroraln(f"wait {sleep_time} s ", "")
            
            if sleep_time == -1:
                #handle convergence
                    #min(_check_convergence() + minWaitTime, maxWaitTime)
                    #for simplicity, maxWaitTime % minWaitTime == 0
                #CLI(net.net)
                erroraln("+ check convergence", "")
                begin_t = time.time()
                while True:
                    if self._check_converge_ospf(net):
                        time.sleep(self.minWaitTime)
                        res[i]['exec']['convergence'] = True
                        warnaln("   + convergence!", "")
                        break
                    else:
                        if time.time() - begin_t >= self.maxWaitTime:
                            res[i]['exec']['convergence'] = False
                            warnaln("   + not convergence!", "")
                            break
                        else:
                            time.sleep(10)
            else:
                time.sleep(sleep_time)
            erroraln("+ collect result", "")
            warnaln("   + collect from daemons", "")
            res[i]['watch'] = {}
            for r_name in self.routers:
                #some routers may be deleted
                if r_name not in net.net.nameToNode:
                    continue
                res[i]['watch'][r_name] = net.net.nameToNode[r_name].dump_info_ospf()
            warnaln("   - collect from daemons", "")
            warnaln("   + collect from asan", "")
            for r_name in self.routers:
                if r_name not in net.net.nameToNode:
                    continue
                net.net.nameToNode[r_name].check_asan()
            warnaln("   - collect from asan", "")
            erroraln("- collect result", "")
        net.stop_net()
        return res
    
    def _run_for_isis(self, r):
        erroraln(f"\n\n======round{r}======","")
        erroraln("+ mininet init", "")
        net = testnet.TestNet()
        erroraln("- mininet init", "")
        ctx = {"intf":{}}
        commands = self.conf['commands'][r]
        res = []
        for i in range(0, self.step_nums[r]):
            erroraln(f"\n\n>>>> + step{i} <<<<", "")


            isis_res = {}
            if i == 0:
                erroraln(f"+ ISIS commands", "")
                for j in range(0, len(self.routers)):
                    router_name = self.routers[j]
                    isis_ops = commands[i]['isis'][j]
                    self._init_isis(router_name, isis_ops)
                erroraln(f"- ISIS commands", "")

                erroraln(f"+ PHY commands", "")
                phy_res = self._run_phy_commands(net, ctx, commands[i]['phy'])
                erroraln(f"- PHY commands", "")

            else:
                erroraln(f"+ PHY commands", "")
                phy_res = self._run_phy_commands(net, ctx, commands[i]['phy'])
                erroraln(f"- PHY commands", "")

                erroraln(f"+ ISIS commands", "")
                for j in range(len(self.routers) -1, -1, -1):
                    router_name = self.routers[j]
                    isis_ops = commands[i]['isis'][j]
                    tmp = self._run_isis_commands(net, router_name, isis_ops)
                    if j == 0:
                        print(tmp)
                    isis_res[router_name] = tmp
                erroraln(f"- ISIS commands", "")

            if i == 0:
                net.start_net()
            res.append({})
            res[i]['exec'] = {}
            res[i]['exec']['phy'] = phy_res
            res[i]['exec']['isis'] = isis_res

            sleep_time = commands[i]['waitTime']
            erroraln(f"wait {sleep_time} s ", "")
            res[i]['database']={}
            if sleep_time == -1:
                #handle convergence
                    #min(_check_convergence() + minWaitTime, maxWaitTime)
                    #for simplicity, maxWaitTime % minWaitTime == 0
                erroraln("+ check convergence", "")
                begin_t = time.time()
                while True:
                    if self._check_converge_isis(net):
                        time.sleep(self.minWaitTime)
                        res[i]['exec']['convergence'] = True
                        warnaln("   + convergence!", "")
                        break
                    else:
                        if time.time() - begin_t >= self.maxWaitTime:
                            res[i]['exec']['convergence'] = False
                            warnaln("   + not convergence!", "")
                            break
                        else:
                            time.sleep(10)
                for r_name in self.routers:
                #some routers may be deleted
                    if r_name not in net.net.nameToNode:
                        continue
                    res[i]['database'][r_name] = net.net.nameToNode[r_name].dump_isis_database()
                    print(net.net.nameToNode[r_name].dump_route_info())
            else:
                time.sleep(sleep_time)
            erroraln("+ collect result", "")
            warnaln("   + collect from daemons", "")
            res[i]['watch'] = {}
            for r_name in self.routers:
                #some routers may be deleted
                if r_name not in net.net.nameToNode:
                    continue
                res[i]['watch'][r_name] = net.net.nameToNode[r_name].dump_info_isis()
            warnaln("   - collect from daemons", "")
            warnaln("   + collect from asan", "")
            for r_name in self.routers:
                if r_name not in net.net.nameToNode:
                    continue
                net.net.nameToNode[r_name].check_asan()
            warnaln("   - collect from asan", "")
            erroraln("- collect result", "")
        net.stop_net()
        return res
    
    def _run_for_rip(self, r):
        erroraln(f"\n\n======round{r}======","")
        erroraln("+ mininet init", "")
        net = testnet.TestNet()
        erroraln("- mininet init", "")
        ctx = {"intf":{}}
        commands = self.conf['commands'][r]
        res = []
        for i in range(0, self.step_nums[r]):
            erroraln(f"\n\n>>>> + step{i} <<<<", "")
            rip_res = {}
            if i == 0:
                erroraln(f"+ RIP commands", "")
                for j in range(0, len(self.routers)):
                    router_name = self.routers[j]
                    rip_ops = commands[i]['rip'][j]
                    self._init_rip(router_name, rip_ops)
                erroraln(f"- RIP commands", "")
                
                erroraln(f"+ PHY commands", "")
                phy_res = self._run_phy_commands(net, ctx, commands[i]['phy'])
                erroraln(f"- PHY commands", "")
            
            else:
                erroraln(f"+ PHY commands", "")
                phy_res = self._run_phy_commands(net, ctx, commands[i]['phy'])
                erroraln(f"- PHY commands", "")

                erroraln(f"+ RIP commands", "")
                for j in range(len(self.routers) -1, -1, -1):
                    router_name = self.routers[j]
                    rip_ops = commands[i]['rip'][j]
                    tmp = self._run_rip_commands(net, router_name, rip_ops)
                    if j == 0:
                        print(tmp)
                    rip_res[router_name] = tmp
                erroraln(f"- RIP commands", "")
            
            if i == 0:    
                net.start_net()
            res.append({})
            res[i]['exec'] = {}
            res[i]['exec']['phy'] = phy_res
            res[i]['exec']['rip'] = rip_res
            
            sleep_time = commands[i]['waitTime']
            erroraln(f"wait {sleep_time} s ", "")
            #CLI(net.net)
            if sleep_time == -1:
                #We wait 30s for RIP to convergence
                CLI(net.net)
                erroraln("+ check convergence", "")
                time.sleep(30)
                warnaln("   + convergence!", "")
            else:
                time.sleep(sleep_time)

            erroraln("+ collect result", "")
            warnaln("   + collect from daemons", "")
            res[i]['watch'] = {}
            for r_name in self.routers:
                #some routers may be deleted
                if r_name not in net.net.nameToNode:
                    continue
                res[i]['watch'][r_name] = net.net.nameToNode[r_name].dump_info_rip()
            warnaln("   - collect from daemons", "")
            warnaln("   + collect from asan", "")
            for r_name in self.routers:
                if r_name not in net.net.nameToNode:
                    continue
                net.net.nameToNode[r_name].check_asan()
            warnaln("   - collect from asan", "")
            erroraln("- collect result", "")
        net.stop_net()
        return res

    def _run_for_babel(self, r):
        erroraln(f"\n\n======round{r}======","")
        erroraln("+ mininet init", "")
        net = testnet.TestNet()
        erroraln("- mininet init", "")
        ctx = {"intf":{}}
        commands = self.conf['commands'][r]
        res = []
        for i in range(0, self.step_nums[r]):
            erroraln(f"\n\n>>>> + step{i} <<<<", "")
            babel_res = {}
            if i == 0:
                erroraln(f"+ BABEL commands", "")
                for j in range(0, len(self.routers)):
                    router_name = self.routers[j]
                    babel_ops = commands[i]['babel'][j]
                    self._init_rip(router_name, babel_ops)
                erroraln(f"- BABEL commands", "")

                erroraln(f"+ PHY commands", "")
                phy_res = self._run_phy_commands(net, ctx, commands[i]['phy'])
                erroraln(f"- PHY commands", "")

            else:
                erroraln(f"+ PHY commands", "")
                phy_res = self._run_phy_commands(net, ctx, commands[i]['phy'])
                erroraln(f"- PHY commands", "")

                erroraln(f"+ BABEL commands", "")
                for j in range(len(self.routers) -1, -1, -1):
                    router_name = self.routers[j]
                    babel_ops = commands[i]['babel'][j]
                    tmp = self._run_babel_commands(net, router_name, babel_ops)
                    if j == 0:
                        print(tmp)
                    babel_res[router_name] = tmp
                erroraln(f"- RIP commands", "")

            if i == 0:
                net.start_net()
            res.append({})
            res[i]['exec'] = {}
            res[i]['exec']['phy'] = phy_res
            res[i]['exec']['babel'] = babel_res

            sleep_time = commands[i]['waitTime']
            erroraln(f"wait {sleep_time} s ", "")
            #CLI(net.net)
            if sleep_time == -1:
                #We wait 30s for RIP to convergence
                #CLI(net.net)
                erroraln("+ check convergence", "")
                time.sleep(30)
                warnaln("   + convergence!", "")
            else:
                time.sleep(sleep_time)

            erroraln("+ collect result", "")
            warnaln("   + collect from daemons", "")
            res[i]['watch'] = {}
            for r_name in self.routers:
                #some routers may be deleted
                if r_name not in net.net.nameToNode:
                    continue
                res[i]['watch'][r_name] = net.net.nameToNode[r_name].dump_info_babel()
            warnaln("   - collect from daemons", "")
            warnaln("   + collect from asan", "")
            for r_name in self.routers:
                if r_name not in net.net.nameToNode:
                    continue
                net.net.nameToNode[r_name].check_asan()
            warnaln("   - collect from asan", "")
            erroraln("- collect result", "")
        net.stop_net()
        return res


    def _run_for_openfabric(self, r):
        erroraln(f"\n\n======round{r}======","")
        erroraln("+ mininet init", "")
        net = testnet.TestNet()
        erroraln("- mininet init", "")
        ctx = {"intf":{}}
        commands = self.conf['commands'][r]
        res = []
        for i in range(0, self.step_nums[r]):
            erroraln(f"\n\n>>>> + step{i} <<<<", "")


            openfabric_res = {}
            if i == 0:
                erroraln(f"+ openfabric commands", "")
                for j in range(0, len(self.routers)):
                    router_name = self.routers[j]
                    openfabric_ops = commands[i]['openfabric'][j]
                    self._init_openfabric(router_name, openfabric_ops)
                erroraln(f"- openfabric commands", "")

                erroraln(f"+ PHY commands", "")
                phy_res = self._run_phy_commands(net, ctx, commands[i]['phy'])
                erroraln(f"- PHY commands", "")

            else:
                erroraln(f"+ PHY commands", "")
                phy_res = self._run_phy_commands(net, ctx, commands[i]['phy'])
                erroraln(f"- PHY commands", "")

                erroraln(f"+ openfabric commands", "")
                for j in range(len(self.routers) -1, -1, -1):
                    router_name = self.routers[j]
                    openfabric_ops = commands[i]['openfabric'][j]
                    tmp = self._run_openfabric_commands(net, router_name, openfabric_ops)
                    if j == 0:
                        print(tmp)
                    openfabric_res[router_name] = tmp
                erroraln(f"- openfabric commands", "")

            if i == 0:
                net.start_net()
            res.append({})
            res[i]['exec'] = {}
            res[i]['exec']['phy'] = phy_res
            res[i]['exec']['openfabric'] = openfabric_res

            sleep_time = commands[i]['waitTime']
            erroraln(f"wait {sleep_time} s ", "")
            res[i]['database']={}
            if sleep_time == -1:
                #handle convergence
                    #min(_check_convergence() + minWaitTime, maxWaitTime)
                    #for simplicity, maxWaitTime % minWaitTime == 0
                erroraln("+ check convergence", "")
                begin_t = time.time()
                while True:
                    if self._check_converge_openfabric(net):
                        time.sleep(self.minWaitTime)
                        res[i]['exec']['convergence'] = True
                        warnaln("   + convergence!", "")
                        break
                    else:
                        if time.time() - begin_t >= self.maxWaitTime:
                            res[i]['exec']['convergence'] = False
                            warnaln("   + not convergence!", "")
                            break
                        else:
                            time.sleep(10)
                for r_name in self.routers:
                #some routers may be deleted
                    if r_name not in net.net.nameToNode:
                        continue
                    res[i]['database'][r_name] = net.net.nameToNode[r_name].dump_openfabric_database()
                    print(net.net.nameToNode[r_name].dump_route_info())
            else:
                time.sleep(sleep_time)
            erroraln("+ collect result", "")
            warnaln("   + collect from daemons", "")
            res[i]['watch'] = {}
            for r_name in self.routers:
                #some routers may be deleted
                if r_name not in net.net.nameToNode:
                    continue
                res[i]['watch'][r_name] = net.net.nameToNode[r_name].dump_info_openfabric()
            warnaln("   - collect from daemons", "")
            warnaln("   + collect from asan", "")
            for r_name in self.routers:
                if r_name not in net.net.nameToNode:
                    continue
                net.net.nameToNode[r_name].check_asan()
            warnaln("   - collect from asan", "")
            erroraln("- collect result", "")
        net.stop_net()
        return res
#================TEST ENTRY====================
    def test(self):
        #FIXME we should add other process
        self.run_pocess = {
            "ospf": self._run_for_ospf,
            "isis": self._run_for_isis,
            "rip": self._run_for_rip,
            "babel": self._run_for_babel,
            "openfabric": self._run_for_openfabric
        }
        try:
            res = {}
            start = time.time()
            res['result'] = []
            for i in range(0, self.round_num):
                # here is isis or ospf
                # isis: _run_for_isis   ospf: _run
                res['result'].append(self.run_pocess[self.protocol](i))
            stop = time.time()
            res['total_test_time'] = stop - start
            self.conf['test'] = res
            result_path = path.join(self.output_dir, f"{self.conf_name}_res.json")
            with open(result_path, "w") as fp:
                json.dump(self.conf, fp)
            return 0
        except Exception as e:
            traceback.print_exception(e)
            os.system("mn -c")
            return -1




if __name__ == "__main__":
    t = executor("/home/frr/topo-fuzz/test/topo_test/data/check/test1742971398_r1.json", "/home/frr/topo-fuzz/test/topo_test/data/result", 1, 60, "babel")
    t.test()