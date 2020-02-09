package com.example.stateswitch;

import android.content.Context;

import androidx.annotation.NonNull;

public interface IngCreator {

	@NonNull
	StateIngInterface createStateIng(@NonNull Context context, @NonNull StateLayout layout);
}
