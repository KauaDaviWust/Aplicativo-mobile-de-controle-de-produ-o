package com.cq.fabrica;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private View taloesView;
    private View tiposView;
    private View adicionarView;
    private View editarView;
    private View finalizarView;
    private Dados db = new Dados(this);
    private AdicionarTalaoControler adicionaTalaoControler;
    private EditarControler editarControler;
    private TaloesControler taloesControler;
    private TipoControler tipoControler;
    private FinalizarControler finalizarControler;
    private Ferramentas ferramentas = new Ferramentas();
    private Button btnFinalizar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Força o modo claro (desativa o modo noturno) para todo o aplicativo
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        btnFinalizar = findViewById(R.id.fechar);


        // Inicializando as três telas
        taloesView = findViewById(R.id.taloes_layout);
        tiposView = findViewById(R.id.tipos_layout);
        adicionarView = findViewById(R.id.adicionar_layout);
        editarView = findViewById(R.id.editar_layout);
        finalizarView = findViewById(R.id.finalizar_layout);


        // Inicializa o controlador uma única vez
        taloesControler = new TaloesControler(taloesView, db);
        adicionaTalaoControler = new AdicionarTalaoControler(adicionarView, db);
        tipoControler = new TipoControler(tiposView, db);
        editarControler = new EditarControler(editarView, db);
        finalizarControler = new FinalizarControler(finalizarView, db);


        btnFinalizar.setOnClickListener(v -> {
            finalizarVisivel();
        });


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_Quinzena) {
                    taloesVisivel();


                }
                else if (id == R.id.nav_tipo) {
                    tiposVisivel();

                }
                else if (id == R.id.nav_adicionar_talao) {
                    adicionarVisivel();
                    ferramentas.CarregarTipos( adicionaTalaoControler.getTipo(), adicionarView, db);

                }
                else if (id == R.id.nav_outras_tabelas) {
                    taloesView.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Quinzenas anteriores", Toast.LENGTH_SHORT).show();

                }

                drawerLayout.closeDrawers();
                return true;
            }
        });



        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (tiposView.getVisibility() == View.VISIBLE || adicionarView.getVisibility() == View.VISIBLE) {
                    tiposView.setVisibility(View.GONE);
                    adicionarView.setVisibility(View.GONE);
                    taloesView.setVisibility(View.VISIBLE);
                } else {
                    finish();
                }
            }
        });

        navigationView.setCheckedItem(R.id.nav_Quinzena);

        navigationView.getMenu().performIdentifierAction(R.id.nav_Quinzena, 0);
    }
    public void removerView(){
        adicionarView.setVisibility(View.GONE);
        tiposView.setVisibility(View.GONE);
        taloesView.setVisibility(View.GONE);
        editarView.setVisibility(View.GONE);
        finalizarView.setVisibility(View.GONE);
    }

    public void EditarVesivel (int id){
        removerView();
        editarView.setVisibility(View.VISIBLE);
        editarControler.carregarDados(id);
        editarControler.carregarPagina();
    }
    public void taloesVisivel(){
        removerView();
        taloesView.setVisibility(View.VISIBLE);

        taloesControler.carregarPagina();
        taloesControler.AtualizarTabela();
    }
    public void tiposVisivel(){
        removerView();
        tiposView.setVisibility(View.VISIBLE);

        tipoControler.carregarPagina();
    }
    public void adicionarVisivel(){
        removerView();
        adicionarView.setVisibility(View.VISIBLE);
        adicionaTalaoControler.carregarPagina();
    }
    public void finalizarVisivel(){
        removerView();
        finalizarView.setVisibility(View.VISIBLE);
        finalizarControler.carregarPagina();
    }
}
