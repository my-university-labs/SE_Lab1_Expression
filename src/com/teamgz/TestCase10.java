package com.teamgz;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCase10 {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Main m = new Main();
		String result = m.derivative("5.22x+4.78x", 'x');
		assertEquals("10.0",result);
	}

}
