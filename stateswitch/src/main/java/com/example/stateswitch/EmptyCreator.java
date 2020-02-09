package com.example.stateswitch;

import android.content.Context;

import androidx.annotation.NonNull;

public interface EmptyCreator {
	@NonNull
	StateEmptyInterface createStateEmpty(@NonNull Context context, @NonNull StateLayout layout);
}
