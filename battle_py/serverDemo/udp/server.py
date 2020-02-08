# -*- coding: utf-8 -*-
from socket import *
from time import ctime

HOST = ''
PORT = 8888
BUFSIZ = 1024
ADDR = (HOST, PORT)

udpSerSock = socket(AF_INET, SOCK_DGRAM)
udpSerSock.bind(ADDR)

while True:
    print('waiting for message...')
    data, addr = udpSerSock.recvfrom(BUFSIZ)

    content = '[%s] %s' % (bytes(ctime(), "utf-8"), data)
    print(data.decode('utf-8'))
    udpSerSock.sendto(content.encode("utf-8"), addr)
    print('...received from and returned to:', addr)

udpSerSock.close()