package com.mashibing.io.chat;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ServerFrame extends Frame {

  public static ServerFrame INSTANCE = new ServerFrame();

  private TextArea rightTa = new TextArea();
  private TextArea leftTa = new TextArea();

  private ServerFrame(){
    this.setSize(1080,720);
    this.setLocation(600,100);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        //todo
        System.exit(0);
      }
    });
    this.setLayout(new GridLayout(1,2));
    this.add(leftTa);
    this.add(rightTa);
  }

  public void updateServerMsg(String msg){
    leftTa.append(msg+System.getProperty("line.separator"));
  }

  public void updateClientMsg(String msg){
    rightTa.append(msg+System.getProperty("line.separator"));
  }

  public static void main(String[] args) {
    ServerFrame.INSTANCE.setVisible(true);
    new ChatServer().start();
  }

}
