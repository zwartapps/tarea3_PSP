package es.formacion.cip;

import java.io.*;
import java.net.*;

public class HiloServidor extends Thread {

    // Atributos
    DataInputStream fentrada;
    Socket socket;

    public HiloServidor(Socket s) {

        socket = s;
        try {
            // Se crea el flujo de entrada
            fentrada = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error de E/S");
            e.printStackTrace();
        }
    } // constructor

    public void run() {

        ServidorChat.mensaje.setText("Numero de Conexiones Actuales: "
                + ServidorChat.ACTUALES);
        // Nada más conectarse el cliente se envia todos los mensajes
        String texto = ServidorChat.textarea.getText();
        EnviarMensajes(texto);
        while (true) {
            String cadena;
            try {
                cadena = fentrada.readUTF(); // Leemos lo que escribe el cliente
                // Cuando un cliente finaliza envia un *
                if (cadena.trim().equals("*")) {
                    ServidorChat.ACTUALES--;
                    ServidorChat.mensaje.setText("Numero de Conexiones Actuales: "
                            + ServidorChat.ACTUALES);
                    break; // Salir del while
                }
                ServidorChat.textarea.append(cadena + "\n");
                texto = ServidorChat.textarea.getText();
                EnviarMensajes(texto); // Se envia el texto a todos los clientes

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        } // while
    } // run

    // Envia los mensajes del TextArea a los clientes de chat
    private void EnviarMensajes(String texto) {

        // Recorremos la tabla de sockets para enviarles los mensajes
        for (int i = 0; i < ServidorChat.CONEXIONES; i++) {
            Socket s = ServidorChat.tabla[i];
            try {
                DataOutputStream fsalida = new DataOutputStream(s.getOutputStream());
                fsalida.writeUTF(texto);
            } catch (SocketException se) {
                // Esta excepcion ocurre cuando escribimos en un socket
                // de un cliente que ha finalizado
                se.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } // for
    } // EnviarMensajes
} // HiloServidor

