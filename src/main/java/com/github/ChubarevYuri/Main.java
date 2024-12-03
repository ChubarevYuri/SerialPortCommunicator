package com.github.ChubarevYuri;

import com.github.ChubarevYuri.DCON.TPA.BFU_GB106v1;
import com.github.ChubarevYuri.DCON.TPA.KS8;
import com.github.ChubarevYuri.Elements.Relay;
import com.github.ChubarevYuri.Modbus.AET.AET421_01C;
import com.github.ChubarevYuri.Modbus.Base;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws PortException, InterruptedException {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Hello World");
        LOG.config("", "log", Charset.defaultCharset(), 10, Level.INFO);

        Port port = new Port("COM3", BaudRate.BPS9600, Parity.NONE, DataBits.EIGHT, StopBits.TWO);
        port.setReadTimeout(300);
        LOG.INFO("hello");

        KS8 dev = new KS8(port,new UByte(1));
        Relay relay = new Relay(dev.out1, false, null);

        Thread.sleep(5000);
        for (int i = 0; i < 15; i ++) {
            relay.set(!relay.get());
            Thread.sleep(500);
        }
        Thread.sleep(5000);
    }
}

