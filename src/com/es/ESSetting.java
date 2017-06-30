package com.es;

	public enum ESSetting {
		CAT ("cat",
			new String[] {
				"10.10.119.11",  "10.10.119.12"},
			false),
		LOCAL ("my-application",
				new String[] {
					"10.4.8.65"},
				false),
		STAR ("star",
			new String[] {
				"10.0.119.13", "10.10.119.166",
			},
			false);
		
		final String name;
		final String[] addresses;
		final boolean isSniff;
		
		private ESSetting(String name, String[] addresses, boolean isSniff) {
			this.name = name;
			this.addresses = addresses;
			this.isSniff = isSniff;
		}	
	}
