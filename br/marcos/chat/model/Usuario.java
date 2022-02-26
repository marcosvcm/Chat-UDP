///////////////////
//MARCOS V////////
/////////////////0 0
////////////////  -

package br.marcos.chat.model;

public class Usuario {
    private int porta;
    private String ip;

    public Usuario(String ip, int porta) {
        this.ip = ip;
        this.porta = porta;
    }

    public Usuario() {
        this.ip = "255.255.255.255";
        this.porta = 12345;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }

    public int getPorta() {
        return this.porta;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return this.ip;
    }

    public boolean equals(Usuario usuario) {
        if (this.ip.contains(usuario.getIp()) && this.porta == usuario.getPorta()) {
            return true;
        } else {
            return false;
        }
    }
}
