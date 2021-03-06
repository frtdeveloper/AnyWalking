package com.lenwotion.travel.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.lenwotion.travel.bean.search.db.SearchBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SEARCH_BEAN".
*/
public class SearchBeanDao extends AbstractDao<SearchBean, Long> {

    public static final String TABLENAME = "SEARCH_BEAN";

    /**
     * Properties of entity SearchBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Type = new Property(1, int.class, "type", false, "TYPE");
        public final static Property Historyword = new Property(2, String.class, "historyword", false, "HISTORYWORD");
        public final static Property Updatetime = new Property(3, long.class, "updatetime", false, "UPDATETIME");
    }


    public SearchBeanDao(DaoConfig config) {
        super(config);
    }
    
    public SearchBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SEARCH_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TYPE\" INTEGER NOT NULL ," + // 1: type
                "\"HISTORYWORD\" TEXT," + // 2: historyword
                "\"UPDATETIME\" INTEGER NOT NULL );"); // 3: updatetime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SEARCH_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, SearchBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getType());
 
        String historyword = entity.getHistoryword();
        if (historyword != null) {
            stmt.bindString(3, historyword);
        }
        stmt.bindLong(4, entity.getUpdatetime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, SearchBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getType());
 
        String historyword = entity.getHistoryword();
        if (historyword != null) {
            stmt.bindString(3, historyword);
        }
        stmt.bindLong(4, entity.getUpdatetime());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public SearchBean readEntity(Cursor cursor, int offset) {
        SearchBean entity = new SearchBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // type
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // historyword
            cursor.getLong(offset + 3) // updatetime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, SearchBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setType(cursor.getInt(offset + 1));
        entity.setHistoryword(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setUpdatetime(cursor.getLong(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(SearchBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(SearchBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(SearchBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
