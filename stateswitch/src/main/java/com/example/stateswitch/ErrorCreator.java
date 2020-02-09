package com.example.stateswitch;

import android.content.Context;

import androidx.annotation.NonNull;

public interface ErrorCreator {

	@NonNull
	StateErrorInterface createStateError(@NonNull Context context, @NonNull StateLayout layout);
}
