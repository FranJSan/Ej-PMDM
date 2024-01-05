package sanchezfernandez.franciscojose.tarea07;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.logging.Logger;

/**
 * Ejercicio 7 -> Generar número aleatorio y guardar en SharedPreferences los acierto del usuario.
 *
 * NOTA: Se puede ver el número secreto pulsando en el icono del corazón.
 *
 * @author fran_
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etNumero;
    private Button btnComprobar;
    private TextView tvPuntos, tvBorrarPuntos;
    private int numeroSecreto;
    private Toast currentToast;

    // Teclado para modo Land
    private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0, btnBorrar;
    private ImageView ivAyuda;

    /**
     * Al crearse la activity se inicializan las views y se genera un número secreto para el juego.
     * También se establecen los puntos almacenados en el SharePreferences
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializarViews();

        /*
        En el modo Land he hecho un teclado númerico porque escribir en ese modo no me gusta, oculta
        toda la activity en muchos dispositivos que no tienen teclado flotante. Crear un teclado numérico
        es una solución rapida que mejora la usabilidad de la app.
        Como este teclado solo existe en el modo Land, solo se inicializa en ese modo.
         */
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            inicializarTecladoVirtual();
        }

        establecerPuntos();
        numeroSecreto = generarNumeroSecreto();
    }

    /**
     * Método para inicializar todas las views de la activity excepto el teclado virtual
     *
     * @see this::inicializarTecladoVirtual
     */
    private void inicializarViews() {
        etNumero = findViewById(R.id.etNumero);
        etNumero.setCursorVisible(false);
        btnComprobar = findViewById(R.id.btnComprobar);
        btnComprobar.setOnClickListener(this);
        tvPuntos = findViewById(R.id.tvPuntos);
        tvBorrarPuntos = findViewById(R.id.tvBorrarPuntos);
        tvBorrarPuntos.setOnClickListener((v) -> resetearPuntos());

        /*
        He incluido una ayuda para hacer las pruebas de la app.
         */
        ivAyuda = findViewById(R.id.ivAyuda);
        ivAyuda.setOnClickListener((v) -> {
            mostrarToast("El número secreto es: " + numeroSecreto, Toast.LENGTH_SHORT);
        });
    }


    /**
     * Método que inicializa todos los buttons del teclado virtual y les asigna un controlador para
     * el evento click.
     */
    private void inicializarTecladoVirtual() {
        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);
        btnBorrar = findViewById(R.id.btnBorrar);

        btn0.setOnClickListener(this::onClickTeclado);
        btn1.setOnClickListener(this::onClickTeclado);
        btn2.setOnClickListener(this::onClickTeclado);
        btn3.setOnClickListener(this::onClickTeclado);
        btn4.setOnClickListener(this::onClickTeclado);
        btn5.setOnClickListener(this::onClickTeclado);
        btn6.setOnClickListener(this::onClickTeclado);
        btn7.setOnClickListener(this::onClickTeclado);
        btn8.setOnClickListener(this::onClickTeclado);
        btn9.setOnClickListener(this::onClickTeclado);
        btnBorrar.setOnClickListener(this::onClickTeclado);

    }

    /**
     * Este método hace de controlador para el evento click del teclado virtual. Según el button pulsado
     * se introduce el número correspondiente o, si es el button "Borrar", borra el último número
     * introducido.
     *
     * @param v button fuente
     */
    private void onClickTeclado(View v) {
        Button btn = (Button) v;

        if (btn == btn0) {
            if (etNumero.getText().length() > 0) {
                etNumero.setText(etNumero.getText().append(btn.getText()));
            }
        } else if (btn == btnBorrar) {
            if (etNumero.getText().length() > 0) {
                String textoET = etNumero.getText().toString();
                textoET = textoET.substring(0, textoET.length() - 1);
                etNumero.setText(textoET);
            }
        } else {
            etNumero.setText(etNumero.getText().append(btn.getText()));
        }
    }

    /**
     * Método que recupera del SharedPrerences los puntos y los settea en tvPuntos
     */
    private void establecerPuntos() {
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        tvPuntos.setText(String.valueOf(sp.getInt("Puntos", 0)));
    }

    /**
     * Método que recupera y modifica los puntos del SharePreferences. Por último, llama al método
     * establecerPuntos() para settear el nuevo valor.
     */
    private void aumentarPuntos() {
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        int puntosAct = Integer.parseInt(tvPuntos.getText().toString());
        editor.putInt("Puntos", puntosAct + 1).apply();
        establecerPuntos();
    }

    /**
     * Método para poner a 0 los puntos almacenados en el SharePreferences.
     */
    private void resetearPuntos() {
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putInt("Puntos", 0).apply();
        establecerPuntos();
    }

    /**
     * Genera y devuelve un int entre 1 y 20.
     *
     * @return número generado
     */
    private int generarNumeroSecreto() {
        Random random = new Random();
        int number = random.nextInt(20) + 1;
        return number;
    }

    /**
     * Método que controla el evento click del button "Comprobar". Comprueba si el valor introducido
     * por el usuario es igual al número secreto. Si falla, se muestra un Toast informando al usuario,
     * y si acierta, se muestra un dialog informando de la victorio y se da a elegir al usuario entre
     * jugar otra partida o salir de la aplicación.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        try {
            int input = Integer.parseInt(etNumero.getText().toString());

            if (input != numeroSecreto) {
                mostrarToast("¡¡Has fallado!!", Toast.LENGTH_SHORT);
                limpiarEtNumero();
            } else {
                aumentarPuntos();
                mostrarDialog();
            }

        } catch (NumberFormatException e) {
            mostrarToast("Error en el número introducido", Toast.LENGTH_LONG);
        }
    }

    /**
     * Método para mostrar un dialogo de alerta ak usuario cuando acierta el número. Se da la opción
     * al usuario de jugar otra partida o de salir de la aplicación.
     */
    private void mostrarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("¡¡¡Has ganado!!!")
                .setMessage("¿Quieres jugar otra?")
                .setPositiveButton("¡Claro!", (dialog, which) -> {
                    numeroSecreto = generarNumeroSecreto();
                    limpiarEtNumero();
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    //dialog.dismiss();
                    finish();
                })
                .setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Este método muestra un Toast con el mensaje y la duración que se pasen como argumentos.
     * El método cancela el Toast que estuviera mostrándose en ese momento. En un principio usaba la
     * clasae android.os.Handler para inciar una tarea pasada la duración del Toast, pero tb vi que
     * podía simplemente llamar al método cancel directamente.
     *
     * @param s
     * @param duration
     */
    private void mostrarToast(String s, int duration) {

        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(this, s, duration);
        currentToast.show();
        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                currentToast = null;
            }
        }, duration == Toast.LENGTH_SHORT ? 2000 : 3500);

         */
    }

    /**
     * Método para limpiar el EtNumero
     */
    private void limpiarEtNumero() {
        etNumero.setText("");
    }
}