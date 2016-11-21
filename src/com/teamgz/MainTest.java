package com.teamgz;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MainTest {
	private String exp;
	private Map<String, Double> cmds = new HashMap<>();
	@Before
	public void setUp() throws Exception {
		exp="3*x/y*10";
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testSimplify() {
		String result = Main.simplify(exp,cmds);
		assertEquals("30.0*x/y",result);
		cmds.put("x", (double) 2);
		result = Main.simplify(exp,cmds);
		assertEquals("60.0/y",result);
		cmds.put("y", (double) 4);
		result = Main.simplify(exp,cmds);
		assertEquals("15.0",result);
		cmds.put("z", (double) 2);
		result = Main.simplify(exp,cmds);
		assertEquals(null,result);
		cmds.put("y", (double) 0);
		result = Main.simplify(exp,cmds);
		assertEquals(" Can not divide zero. Error, Please check",result);
	}



}
