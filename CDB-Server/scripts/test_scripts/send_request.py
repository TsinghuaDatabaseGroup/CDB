import sys
import socket

host = "127.0.0.1"
port = 1234

def send_request():
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    print "start socket server"
    s.connect((host, port))
    print "bind {}:{}".format(host, port)

    if len(sys.argv) != 2:
        print "need one param, python send_request.py [idx]"
    queryId = sys.argv[1]
    s.send(queryId)
    print "send {}".format(queryId)


if __name__ == '__main__':
    send_request()


