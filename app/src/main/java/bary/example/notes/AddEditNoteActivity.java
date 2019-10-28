package bary.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

public class AddEditNoteActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "bary.example.notes.EXTRA_ID";
    public static final String EXTRA_TITLE = "bary.example.notes.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "bary.example.notes.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY = "bary.example.notes.EXTRA_PRIORITY";

    private EditText edTitle;
    private EditText edDesc;
    private NumberPicker numberPicker;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_edit_note );

        initFields();

        getSupportActionBar().setHomeAsUpIndicator( R.drawable.ic_close_black );

        Intent intent = getIntent();

        if ( intent.hasExtra( EXTRA_ID ) ) {
            setTitle( "Edit note" );
            edTitle.setText( intent.getStringExtra( EXTRA_TITLE ) );
            edDesc.setText( intent.getStringExtra( EXTRA_DESCRIPTION ));
            numberPicker.setValue( intent.getIntExtra( EXTRA_PRIORITY, 1 ) );
        } else {
            setTitle( "Add note" );
        }
    }

    private void initFields() {
        edTitle = findViewById( R.id.ed_title );
        edDesc = findViewById( R.id.ed_description );
        numberPicker = findViewById( R.id.number_picker );

        numberPicker.setMinValue( 1 );
        numberPicker.setMaxValue( 10 );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate( R.menu.add_note_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {
        switch ( item.getItemId() ) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }

    private void saveNote() {
        String title = edTitle.getText().toString();
        String desc = edDesc.getText().toString();
        int priority = numberPicker.getValue();

        if ( title.trim().isEmpty() || desc.trim().isEmpty() ) {
            Toast.makeText( this, "Insert title and description", Toast.LENGTH_SHORT ).show();
            return;
        }


        Intent data = new Intent();
        data.putExtra( EXTRA_TITLE, title );
        data.putExtra( EXTRA_DESCRIPTION, desc );
        data.putExtra( EXTRA_PRIORITY, priority );

        int id = getIntent().getIntExtra( EXTRA_ID, -1 );
        if( id != -1 ){
            data.putExtra( EXTRA_ID, id );
        }

        setResult( RESULT_OK, data );
        finish();
    }
}
