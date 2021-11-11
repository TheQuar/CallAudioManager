package com.quar.turnoffsilentmode.room_database;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"name"}, unique = true)})
public class NumbersTable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "phone_number")
    private String phone_number;

    @ColumnInfo(name = "voice_mode")
    private Boolean voice_mode;

    public NumbersTable(int id, String name, String phone_number, Boolean voice_mode) {
        this.id = id;
        this.name = name;
        this.phone_number = phone_number;
        this.voice_mode = voice_mode;
    }

    @Ignore
    public NumbersTable(String name, String phone_number, Boolean voice_mode) {
        this.name = name;
        this.phone_number = phone_number;
        this.voice_mode = voice_mode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public Boolean getVoice_mode() {
        return voice_mode;
    }

    public void setVoice_mode(Boolean voice_mode) {
        this.voice_mode = voice_mode;
    }


}
