import RPi.GPIO as GPIO
import time
servoPIN = 4
GPIO.setmode(GPIO.BCM)
GPIO.setup(servoPIN, GPIO.OUT)
p = GPIO.PWM(servoPIN, 50)
p.start(2.5) #initialization
def serv(angle):
    A = ((angle/180)*10)+2.5
    #B = A*10//10
    #print(B)
    try:
        p.ChangeDutyCycle(A)
    
    except KeyboardInterrupt:
        p.stop()
        GPIO.cleanup()
