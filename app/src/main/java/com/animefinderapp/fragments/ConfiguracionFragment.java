package com.animefinderapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.animefinderapp.R;
import com.animefinderapp.actividades.ConfiguracionActivity;
import com.animefinderapp.baseDatos.AnimeDataSource;
import com.animefinderapp.entidades.Anime;
import com.animefinderapp.servicios.AnimeService;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by D2 on 24/10/2016.
 */

public class ConfiguracionFragment extends PreferenceFragment {
    private Context context;
    private String server;
    private SharedPreferences sharedPref;

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        server = sharedPref.getString("pref_servidor", "AnimeFlv").toLowerCase();
        addPreferencesFromResource(R.xml.settings);
        Preference buttonExportar = getPreferenceManager().findPreference("subir");
        if (buttonExportar != null) {
            buttonExportar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    AnimeDataSource.uploadDB(context, FirebaseAuth.getInstance().getCurrentUser());
                    return true;
                }
            });
        }
        Preference buttonBorrarImagenes = getPreferenceManager().findPreference("borrarCache");
        if (buttonBorrarImagenes != null) {
            buttonBorrarImagenes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.get(context).clearDiskCache();
                        }
                    }).start();
                    Snackbar.make(view, "Imagenes Eliminadas", Snackbar.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
        Preference buttonBorrarHistorial = getPreferenceManager().findPreference("borrarHistorial");
        if (buttonBorrarHistorial != null) {
            buttonBorrarHistorial.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {

                    if (AnimeDataSource.vaciarHistorial(server, context)) {
                        Snackbar.make(view, "HistorialFragment Eliminado", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(view, "HistorialFragment No Eliminado", Snackbar.LENGTH_SHORT).show();
                    }

                    return true;
                }
            });
        }
        Preference buttonImportar = getPreferenceManager().findPreference("sincronizar");
        if (buttonImportar != null) {
            buttonImportar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(context);
                    dialogo1.setTitle("Informacion:");
                    dialogo1.setMessage(
                            "Se remplazaran todos los datos de la aplicacion,los datos anteriores se perderan.\n¿Desea Continuar?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogo1, int id) {
                            AnimeDataSource.downloadDB(context, FirebaseAuth.getInstance().getCurrentUser());
                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogo1, int id) {
                        }
                    });
                    dialogo1.show();
                    return true;
                }
            });
        }
        Preference buttonBorrar = getPreferenceManager().findPreference("borrarDatos");
        if (buttonBorrar != null) {
            buttonBorrar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(context);
                    dialogo1.setTitle("Informacion:");
                    dialogo1.setMessage(
                            "Se borraran todos los datos de la aplicacion,los datos anteriores se perderan.\n ¿Desea Continuar?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogo1, int id) {

                            if (AnimeDataSource.deleteDB(context)) {
                                Snackbar.make(view, "Datos Eliminados", Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(view, "No se eliminaron los datos.", Snackbar.LENGTH_SHORT).show();
                            }

                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogo1, int id) {
                        }
                    });
                    dialogo1.show();
                    return true;
                }
            });

            Preference notificacioncheckbox = getPreferenceManager().findPreference("notificarcheckbox");
            if (notificacioncheckbox != null) {

                notificacioncheckbox.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference preference) {

                        if (getPreferenceManager().getSharedPreferences().getBoolean("notificarcheckbox", false)) {
                            context.startService(new Intent(context, AnimeService.class));

                            Snackbar.make(view, "Servicio Iniciado", Snackbar.LENGTH_SHORT).show();
                        } else {
                            context.stopService(new Intent(context, AnimeService.class));
                            Snackbar.make(view, "Servicio Detenido", Snackbar.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
            }

        }
        super.onViewCreated(view, savedInstanceState);
    }
}