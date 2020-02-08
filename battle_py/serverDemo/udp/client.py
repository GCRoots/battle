# coding=utf-8
from socket import *

HOST = 'localhost'
PORT = 8888
BUFSIZ = 1024
ADDR = (HOST, PORT)

udpCliSock = socket(AF_INET, SOCK_DGRAM)

while True:
    data = input('> ')
    if not data:
        data='断开连接'
        udpCliSock.sendto(data.encode("utf-8"), ADDR)
        break

    udpCliSock.sendto(data.encode("utf-8"), ADDR)
    data, ADDR = udpCliSock.recvfrom(BUFSIZ)
    if not data:
        break
    print(data)

udpCliSock.close()