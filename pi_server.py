import Servomotor
from socket import *
from time import ctime
import RPi.GPIO as GPIO

#Servomotor.setup()
ctrCmd = ['Up','Down']
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
    print(Dat)
    
    try:
        while True:
            #DataBytes = tcpCliSock.recv(BUFSIZE)
            data = Dat
        
            
            if (data != 'Up') and (data != 'Down'):
                print("Oops!")
                break
                
            if (data == 'Down'):
                #Servomotor.ServoUp()
                print('Decrease: ')#,Servomotor.cur_X)
                break
            if (data == 'Up'):
                print('Increase')
                break
            if (data == '32'):
                #Servomotor.ServoDown()
                num = int(data)
                print(num*2)#,Servomotor.cur_X)
                break
                
    except KeyboardInterrupt:
        #Servomotor.close()
        GPIO.cleanup()
tcpSerSock.close()
            
            
