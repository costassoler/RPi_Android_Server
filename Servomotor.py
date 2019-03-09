import RPi.GPIO as GPIO
import time
cur_X = 0 #initial value

def setup():
    GPIO.setmode(GPIO.BOARD)
    GPIO.setup(11,GPIO.OUT)
    global servo
    servo=GPIO.PWM(11,50)#create PWM on pin 11 with frequency of 50Hz
    servo.start(2.5)
    servo.ChangeDutyCycle(2.5)
    #start pwm with Duty Cycle is 2%, pulse width = 2% = 0.4ms

def ServoDown():
    global cur_X
    cur_X -= 2.5
    if cur_X<2.5:
        cur_X = 2.5
    servo.ChangeDutyCycle(cur_X)
    time.sleep(1)
def close():
    servo.stop()
if __name__=='__main__':
    setup()
    
