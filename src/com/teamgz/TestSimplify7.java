package com.teamgz;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSimplify7 {
	private String exp;
	private Map<String, Double> cmds = new HashMap<>();
	@Before
	public void setUp() throws Exception {
		exp="x/0";
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSimplify() {
		String result = Main.simplify(exp,cmds);
		assertEquals("Infinity*x",result);
	}

}
