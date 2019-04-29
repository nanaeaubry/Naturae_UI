package com.example.naturae_ui.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.examples.naturaeproto.Naturae;

public class Post implements Parcelable {
	public String title;
	public String species;
	public String description;
	public float lat;
	public float lng;
	public String encodedImage;

	public Post(){}

	public Post(Naturae.PostStruct postStruct){
		title = postStruct.getTitle();
		species = postStruct.getSpecies();
		description = postStruct.getDescription();
		lat = postStruct.getLat();
		lng = postStruct.getLng();
		encodedImage = postStruct.getEncodedImage();
	}

	protected Post(Parcel in) {
		title = in.readString();
		species = in.readString();
		description = in.readString();
		lat = in.readFloat();
		lng = in.readFloat();
		encodedImage = in.readString();
	}

	public static final Creator<Post> CREATOR = new Creator<Post>() {
		@Override
		public Post createFromParcel(Parcel in) {
			return new Post(in);
		}

		@Override
		public Post[] newArray(int size) {
			return new Post[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(species);
		dest.writeString(description);
		dest.writeFloat(lat);
		dest.writeFloat(lng);
		dest.writeString(encodedImage);
	}
}
