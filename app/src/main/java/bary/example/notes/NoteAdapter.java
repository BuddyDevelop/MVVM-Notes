package bary.example.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteHolder> {
    private OnItemClickListener listener;

    public NoteAdapter() {
        super( DIFF_CALLBACK );
    }

    //we make this fields static cuz we want to pass it to super() before adapter constructor is finished
    //if it's not static we couldn't access it until constuctor is finish
    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame( @NonNull Note oldItem, @NonNull Note newItem ) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame( @NonNull Note oldItem, @NonNull Note newItem ) {
            return oldItem.getTitle().equals( newItem.getTitle() ) &&
                    oldItem.getDescription().equals( newItem.getDescription() ) &&
                    oldItem.getPriority() == newItem.getPriority();
        }
    };

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        View itemView = LayoutInflater.from( parent.getContext() )
                .inflate( R.layout.note_item, parent, false );

        return new NoteHolder( itemView );
    }

    @Override
    public void onBindViewHolder( @NonNull NoteHolder holder, int position ) {
        Note currentNote = getItem( position );
        holder.tvTitle.setText( currentNote.getTitle() );
        holder.tvDesc.setText( currentNote.getDescription() );
        holder.tvPriority.setText( String.valueOf( currentNote.getPriority() ) );
    }

    public Note getNote( int position ) {
        return getItem( position );
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvDesc;
        private TextView tvPriority;

        public NoteHolder( View itemView ) {
            super( itemView );
            tvTitle = itemView.findViewById( R.id.title );
            tvDesc = itemView.findViewById( R.id.description );
            tvPriority = itemView.findViewById( R.id.priority );

            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View view ) {
                    if ( listener != null && getAdapterPosition() != RecyclerView.NO_POSITION )
                        listener.onItemClick( getNote( getAdapterPosition() ) );
                }
            } );
        }

    }

    public interface OnItemClickListener {
        void onItemClick( Note note );
    }

    public void setOnItemClickListener( OnItemClickListener listener ) {
        this.listener = listener;

    }

}
