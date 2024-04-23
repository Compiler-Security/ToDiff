from mininet.log import  setLogLevel, info, debug, warn, error

def infoln(msg, *args, **kwargs):
    info(msg, *args, **kwargs)
    info("\n")

def infoaln(attri, msg, *args, **kwargs):
    infoln("*** \033[1;34m" + attri + "\033[0m")
    infoln(msg, *args, **kwargs)

def debugln(msg, *args, **kwargs):
    debug(msg + "\n", *args, **kwargs)


def warnln(msg, *args, **kwargs):
    warn(msg, *args, **kwargs)
    warn("\n")

def warnaln(attri, msg, *args, **kwargs):
    warn("*** \033[1;34m" + attri + "\033[0m")
    warnln(msg, *args, **kwargs)
