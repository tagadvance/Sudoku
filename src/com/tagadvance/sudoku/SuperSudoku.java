package com.tagadvance.sudoku;

import java.util.HashSet;
import java.util.Set;

class SuperSudoku extends Sudoku {

	public static final int SUPER_WIDTH = 16, SUPER_HEIGHT = 16;

	public SuperSudoku() {
		super(SUPER_WIDTH, SUPER_HEIGHT);
	}

	public SuperSudoku(Sudoku parent) {
		super(parent);
	}

	@Override
	protected char[] getPossibleValues() {
		// often uses 1 through G rather than the 0 through F used in
		// hexadecimal
		return "abcdefghijklmnop".toCharArray();
	}

	@Override
	protected Set<Scope> createScopes() {
		Set<Scope> scopes = new HashSet<Scope>();

		// add blocks
		int horizontalRegions = 4, verticalRegions = 4, regionWidth = 4, regionHeight = 4;
		for (int y = 0, v = 0; v < verticalRegions; y += regionHeight, v++) {
			for (int x = 0, h = 0; h < horizontalRegions; x += regionWidth, h++) {
				Scope scope = new RectangleScope(this, x, y, regionWidth, regionHeight);
				scopes.add(scope);
			}
		}

		// add columns
		int x = 0, y = 0;
		regionWidth = 1;
		for (x = 0; x < SUPER_WIDTH; x++) {
			Scope scope = new RectangleScope(this, x, y, regionWidth, SUPER_HEIGHT);
			scopes.add(scope);
		}

		// add rows
		x = 0;
		y = 0;
		regionHeight = 1;
		for (y = 0; y < SUPER_HEIGHT; y++) {
			Scope scope = new RectangleScope(this, x, y, SUPER_WIDTH, regionHeight);
			scopes.add(scope);
		}
		assert scopes.size() == 48;

		return scopes;
	}

	/**
	 * 572,514 guesses in 2293.467 seconds (38.2 minutes) @ 2.6 GHz j p i m k g e n f o l a h d c b
	 * l g c e p d h a k b m i f n j o o n k h b c l f j e d p i a m g b d f a i j m o g c n h k l e
	 * p g k j b o e c m n p i f a h l d a h p d g i j l b k e c m o f n n e m f d p a k o h g l b j
	 * i c c i o l f h n b a d j m p k g e d c e p l a k h i m f o g b n j f j h o m b p g l n k e d
	 * c a i k l b g e n d i h a c j o m p f m a n i c f o j d g p b l e h k e b a j n k g p m i h d
	 * c f o l i o l c j m b e p f a k n g d h h f d n a o i c e l b g j p k m p m g k h l f d c j o
	 * n e i b a
	 */
	public static Sudoku demo() {
		Sudoku sudoku = new SuperSudoku();
		String puzzle = "jp????????la???b???e?d??kbm?f?j??nk?bcl?j????a?g??f?i?mog??h?l??"
				+ "gkjb???????f??lda?p????lbke????n??????a??h????i??i????n?adj??kg?"
				+ "?ce??akh?m????n??j????p??n??????k????ndih????m?fma??c???????lehk"
				+ "??a?n??pmi?d?f??i?l????e?fak?gd??f?n?oic??b?j???p???hl????????ba";
		return demo(sudoku, puzzle);
	}

	// http://www.live-sudoku.com/play-online/geant/expert
	public static Sudoku demo2() {
		return Sudoku.demo(new SuperSudoku(),
				"6B??5??????D??7G??E???1??F???8???9?3G??C8??5A?B???AGB3?27?1946?????????4B????????4?92??5C??E6?A????B?C????2?E??????D3EF??4715??????5?4?92?F?3????1G?C??????3?A8?4??AE??????71??93?C87A????9624?BCF???D????A???E5?A???G5??ED???3?8?5???9??7???1?D??24????????GF??");
	}

	public Object clone() {
		return new SuperSudoku(this);
	}

}
