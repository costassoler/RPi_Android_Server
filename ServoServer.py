from socket import *
from time import ctime
import RPi.GPIO as GPIO
import Servo

HOST = ''
PORT = 21567
BUFSIZE = 1024
ADDR = (HOST,PORT)
tcpSerSock = socket(AF_INET,SOCK_STREAM)
tcpSerSock.bind(ADDR)
tcpSerSock.listen(5)

while True:
    tcpCliSock,addr = tcpSerSock.accept()
    print('Waiting for connection')
    Dat = tcpCliSock.recv(BUFSIZE).decode("utf-8")
    print('...connected from :',addr)
    #print(Dat)
    
    try:
        while True:
            data = int(Dat)
            if (data > 0) and (data < 180):
                Servo.serv(data)
                print(Dat)
                break
            else:
                break
            
                
            
                
    except KeyboardInterrupt:
        #Servomotor.close()
        #GPIO.cleanup()
        print("oops")
        

tcpSerSock.close()
            
            
