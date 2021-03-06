package io.openems.edge.ess.api;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.math3.optim.linear.Relationship;
import org.junit.Test;

import io.openems.edge.ess.power.api.CircleConstraint;
import io.openems.edge.ess.power.api.ConstraintType;
import io.openems.edge.ess.power.api.Phase;
import io.openems.edge.ess.power.api.Power;
import io.openems.edge.ess.power.api.Pwr;

public class PowerTest {

	public static final double DELTA_IN_PERCENT = 10; //

	private static final double ZERO_TOLERANCE = 0;

	Power sut;

	@Test
	public void testSymmetric() throws Exception {
		ManagedSymmetricEss ess0 = new ManagedSymmetricEssDummy() {

			@Override
			public void applyPower(int activePower, int reactivePower) {
				assertEquals(1000, activePower);
				assertEquals(500, reactivePower);
			}

		};

		ess0.addPowerConstraint(ConstraintType.CYCLE, Phase.ALL, Pwr.ACTIVE, Relationship.EQ, 1000);
		ess0.addPowerConstraint(ConstraintType.CYCLE, Phase.ALL, Pwr.REACTIVE, Relationship.EQ, 500);

		ess0.getPower().applyPower();
	}

	@Test
	public void testAsymmetric() throws Exception {
		ManagedAsymmetricEss ess0 = new ManagedAsymmetricEssDummy() {

			@Override
			public void applyPower(int activePowerL1, int reactivePowerL1, int activePowerL2, int reactivePowerL2,
					int activePowerL3, int reactivePowerL3) {
				assertEquals(1000, activePowerL1 + activePowerL2 + activePowerL3);
				assertEquals(500, reactivePowerL1 + reactivePowerL2 + reactivePowerL3);
			}

		};

		ess0.addPowerConstraint(ConstraintType.CYCLE, Phase.ALL, Pwr.ACTIVE, Relationship.EQ, 1000);
		ess0.addPowerConstraint(ConstraintType.CYCLE, Phase.ALL, Pwr.REACTIVE, Relationship.EQ, 500);

		ess0.getPower().applyPower();
	}

	@Test
	public void testCluster() throws Exception {
		AtomicInteger totalActivePower = new AtomicInteger();
		AtomicInteger totalReactivePower = new AtomicInteger();

		SymmetricEss ess1 = new ManagedSymmetricEssDummy() {

			@Override
			public void applyPower(int activePower, int reactivePower) {
				totalActivePower.addAndGet(activePower);
				totalReactivePower.addAndGet(reactivePower);
			}
		};

		AsymmetricEss ess2 = new ManagedAsymmetricEssDummy() {

			@Override
			public void applyPower(int activePowerL1, int reactivePowerL1, int activePowerL2, int reactivePowerL2,
					int activePowerL3, int reactivePowerL3) {
				totalActivePower.addAndGet(activePowerL1 + activePowerL2 + activePowerL3);
				totalReactivePower.addAndGet(reactivePowerL1 + reactivePowerL2 + reactivePowerL3);
			}
		};

		EssClusterDummy ess0 = new EssClusterDummy(ess1, ess2);

		ess0.addPowerConstraint(ConstraintType.CYCLE, Phase.ALL, Pwr.ACTIVE, Relationship.EQ, 1000);
		ess0.addPowerConstraint(ConstraintType.CYCLE, Phase.ALL, Pwr.REACTIVE, Relationship.EQ, 500);

		ess0.getPower().applyPower();

		assertEquals(1000, totalActivePower.get());
		assertEquals(500, totalReactivePower.get());
	}

	@Test
	public void testClusterDistribution() throws Exception {
		SymmetricEss ess1 = new ManagedSymmetricEssDummy() {

			@Override
			public void applyPower(int activePower, int reactivePower) {
				assertEquals(500, activePower);
				assertEquals(250, activePower);
			}
		};

		AsymmetricEss ess2 = new ManagedAsymmetricEssDummy() {

			@Override
			public void applyPower(int activePowerL1, int reactivePowerL1, int activePowerL2, int reactivePowerL2,
					int activePowerL3, int reactivePowerL3) {
				assertEquals(500, activePowerL1 + activePowerL2 + activePowerL3);
				assertEquals(250, activePowerL1 + activePowerL2 + activePowerL3);
			}
		};

		EssClusterDummy ess0 = new EssClusterDummy(ess1, ess2);

		ess0.addPowerConstraint(ConstraintType.CYCLE, Phase.ALL, Pwr.ACTIVE, Relationship.EQ, 1000);
		ess0.addPowerConstraint(ConstraintType.CYCLE, Phase.ALL, Pwr.REACTIVE, Relationship.EQ, 500);

		ess0.getPower().applyPower();
	}

	@Test
	public void testMaxActivePower() throws Exception {
		ManagedAsymmetricEss ess0 = new ManagedAsymmetricEssDummy() {

			@Override
			public void applyPower(int activePowerL1, int reactivePowerL1, int activePowerL2, int reactivePowerL2,
					int activePowerL3, int reactivePowerL3) {
			}

		};

		new CircleConstraint(ess0, 1000);

		assertEquals(1000, ess0.getPower().getMaxActivePower());
	}
	
	@Test
	public void testMinActivePower() throws Exception {
		ManagedAsymmetricEss ess0 = new ManagedAsymmetricEssDummy() {

			@Override
			public void applyPower(int activePowerL1, int reactivePowerL1, int activePowerL2, int reactivePowerL2,
					int activePowerL3, int reactivePowerL3) {
			}

		};

		new CircleConstraint(ess0, 1000);

		assertEquals(-1000, ess0.getPower().getMinActivePower());
	}

//	@Test
//	public void testSetActiveAndReactiveToZero() {
//		int givenActivePower = 0;
//		int givenReactivePower = 0;
//
//		ManagedSymmetricEssDummy ess = createSymmetricEss(givenActivePower, givenReactivePower, ZERO_TOLERANCE);
//		sut = new Power(ess);
//
//		try {
//			sut.setActivePowerAndSolve(ConstraintType.STATIC, Relationship.EQ, 0);
//			sut.setReactivePower(ConstraintType.STATIC, Relationship.EQ, 0);
//			sut.applyPower();
//
//		} catch (PowerException e) {
//			fail(e.getMessage());
//		}
//	}
//
//	@Test
//	public void testSetActivePower() {
//		int givenActivePower = 1000;
//		int givenReactivePower = 0;
//
//		ManagedSymmetricEssDummy ess = createSymmetricEss(givenActivePower, givenReactivePower, ZERO_TOLERANCE);
//
//		sut = new Power(ess);
//		try {
//			sut.setActivePowerAndSolve(ConstraintType.STATIC, Relationship.GEQ, givenActivePower);
//			sut.setReactivePower(ConstraintType.STATIC, Relationship.EQ, givenReactivePower);
//			sut.applyPower();
//
//		} catch (PowerException e) {
//			fail(e.getMessage());
//		}
//	}
//
//	@Test
//	public void testApparentPowerAndActivePower() {
//		int maxApparentPower = 2000;
//		int givenActivePower = 1000;
//		int givenReactivePower = 0;
//
//		ManagedSymmetricEssDummy ess0 = createSymmetricEss(givenActivePower, givenReactivePower, DELTA_IN_PERCENT);
//
//		sut = new Power(ess0);
//		try {
//			sut.setActivePowerAndSolve(ConstraintType.STATIC, Relationship.GEQ, givenActivePower);
//			sut.setReactivePower(ConstraintType.STATIC, Relationship.EQ, 0);
//			sut.setMaxApparentPower(ess0, maxApparentPower);
//			sut.applyPower();
//		} catch (PowerException e) {
//			fail(e.getMessage());
//		}
//	}
//
//	@Test // TODO
//	public void testGivenPowerGreaterThanMaxApparentPower() {
//		int maxApparentPower = 1000;
//		int givenActivePower = 2000;
//		int givenReactivePower = 0;
//
//		ManagedSymmetricEssDummy ess = createSymmetricEss(givenActivePower, givenReactivePower, DELTA_IN_PERCENT);
//
//		sut = new Power(ess);
//		try {
//			sut.setActivePowerAndSolve(ConstraintType.STATIC, Relationship.GEQ, givenActivePower); // TODO LEQ?
//			sut.setReactivePower(ConstraintType.STATIC, Relationship.EQ, 0);
//			sut.setMaxApparentPower(ess, maxApparentPower);
//			sut.applyPower();
//		} catch (PowerException e) {
//			fail(e.getMessage());
//		}
//	}
//
//	@Test // TODO
//	public void testGivenPowerAndReactivePower() {
//		int maxApparentPower = 2000;
//		int givenActivePower = 1800;
//		int givenReactivePower = 1000;
//
//		// fails currently, because reactive power result was -835 => Math.sqrt(1800� +
//		// (-835)�) = 1984, should we work with a delta percent=?
//		// awaited result Math.sqrt(2000� - 1800�) = +/-871,779... Difference =
//		// 36,779... ==> 36,../871,.. * 100 = 4,21...%
//		// do i know if it was plus or minus?
//		ManagedSymmetricEssDummy ess = createSymmetricEss(givenActivePower, givenReactivePower, DELTA_IN_PERCENT);
//		sut = new Power(ess);
//
//		try {
//			sut.setActivePowerAndSolve(ConstraintType.STATIC, Relationship.GEQ, 0);
//			sut.setActivePowerAndSolve(ConstraintType.STATIC, Relationship.GEQ, givenActivePower);
//			sut.setReactivePower(ConstraintType.STATIC, Relationship.LEQ, givenReactivePower);
//			sut.setMaxApparentPower(ess, maxApparentPower);
//			sut.applyPower();
//		} catch (PowerException e) {
//			fail(e.getMessage());
//		}
//	}
//
//	@Test
//	public void testAsymmetricAllSetToZero() {
//		int givenActivePowerL1 = 0;
//		int givenActivePowerL2 = 0;
//		int givenActivePowerL3 = 0;
//
//		int givenReactivePowerL1 = 0;
//		int givenReactivePowerL2 = 0;
//		int givenReactivePowerL3 = 0;
//
//		ManagedAsymmetricEssDummy ess = createAsymmetricEss(
//				Arrays.asList(givenActivePowerL1, givenActivePowerL2, givenActivePowerL3),
//				Arrays.asList(givenReactivePowerL1, givenReactivePowerL2, givenReactivePowerL3), DELTA_IN_PERCENT);
//
//		sut = new Power(ess);
//		try {
//			ConstraintType constraintType = ConstraintType.STATIC;
//			Relationship relationship = Relationship.EQ;
//
//			sut.setActivePowerAndSolve(constraintType, relationship, ess, Phase.L1, givenActivePowerL1);
//			sut.setActivePowerAndSolve(constraintType, relationship, ess, Phase.L2, givenActivePowerL2);
//			sut.setActivePowerAndSolve(constraintType, relationship, ess, Phase.L3, givenActivePowerL3);
//			sut.setReactivePowerAndSolve(ConstraintType.STATIC, Relationship.EQ, 0);
//			sut.applyPower();
//
//		} catch (PowerException e) {
//			fail(e.getMessage());
//		}
//	}
//
//	@Test
//	public void testAsymmetricNoRestrictions() {
//		int givenActivePowerL1 = 1000;
//		int givenActivePowerL2 = 800;
//		int givenActivePowerL3 = 1200;
//
//		ManagedAsymmetricEssDummy ess = createAsymmetricEss(
//				Arrays.asList(givenActivePowerL1, givenActivePowerL2, givenActivePowerL3), Arrays.asList(0, 0, 0),
//				DELTA_IN_PERCENT);
//
//		sut = new Power(ess);
//		try {
//			ConstraintType constraintType = ConstraintType.STATIC;
//			Relationship relationship = Relationship.EQ;
//
//			sut.setActivePowerAndSolve(constraintType, relationship, ess, Phase.L1, givenActivePowerL1);
//			sut.setActivePowerAndSolve(constraintType, relationship, ess, Phase.L2, givenActivePowerL2);
//			sut.setActivePowerAndSolve(constraintType, relationship, ess, Phase.L3, givenActivePowerL3);
//			sut.setReactivePower(ConstraintType.STATIC, Relationship.EQ, 0);
//			sut.applyPower();
//
//		} catch (PowerException e) {
//			fail(e.getMessage());
//		}
//	}
//
//	@Test
//	public void testApparentPowerSmallerThanThreeTimesActivePower() {
//		int maxApparentPower = 3000;
//		int givenActivePowerL1 = 2000;
//		int givenActivePowerL2 = 5000;
//		int givenActivePowerL3 = 6000;
//		int givenReactivePowerL1 = 2000;
//		int givenReactivePowerL2 = 2000;
//		int givenReactivePowerL3 = 2000;
//
//		ManagedAsymmetricEssDummy ess0 = new ManagedAsymmetricEssDummy() {
//			@Override
//			public void applyPower(int activePowerL1, int reactivePowerL1, int activePowerL2, int reactivePowerL2,
//					int activePowerL3, int reactivePowerL3) {
//				assertEquals(givenActivePowerL1, activePowerL1);
//				assertEquals(givenActivePowerL2, activePowerL2);
//				assertEquals(givenActivePowerL3, activePowerL3);
//
//				assertEquals(givenReactivePowerL1, reactivePowerL1);
//				assertEquals(givenReactivePowerL2, reactivePowerL2);
//				assertEquals(givenReactivePowerL3, reactivePowerL3);
//			}
//
//		};
//
//		sut = new Power(ess0);
//		try {
//			sut.setMaxApparentPower(ess0, maxApparentPower);
//
//			sut.setActivePowerAndSolve(ConstraintType.STATIC, Relationship.GEQ, ess0, Phase.L1, givenActivePowerL1);
//			sut.setActivePowerAndSolve(ConstraintType.STATIC, Relationship.GEQ, ess0, Phase.L2, givenActivePowerL2);
//			sut.setActivePowerAndSolve(ConstraintType.STATIC, Relationship.GEQ, ess0, Phase.L3, givenActivePowerL3);
//
//			sut.setReactivePowerAndSolve(ConstraintType.STATIC, Relationship.GEQ, ess0, Phase.L1, givenReactivePowerL1);
//			sut.setReactivePowerAndSolve(ConstraintType.STATIC, Relationship.GEQ, ess0, Phase.L2, givenReactivePowerL2);
//			sut.setReactivePowerAndSolve(ConstraintType.STATIC, Relationship.GEQ, ess0, Phase.L3, givenReactivePowerL3);
//
//			sut.applyPower();
//		} catch (PowerException e) {
//			fail(e.getMessage());
//		}
//	}
//
//	public boolean isBetween(double value, double bottomLimit, double upperLimit) {
//		return value > bottomLimit && value < upperLimit;
//	}
//
//	void checkValues(int expectedActive, int expectedReactive, int actualActive, int actualReactive,
//			double deltaPercent) {
//		double deltaActive = expectedActive * deltaPercent / 100;
//		double deltaReactive = expectedReactive * deltaPercent / 100;
//		assertEquals(expectedActive, actualActive, deltaActive);
//		assertEquals(expectedReactive, actualReactive, deltaReactive);
//	}
//
//	void checkValues(Map<Integer, Integer> expectedActualValues, double deltaPercent) {
//		for (Entry<Integer, Integer> entry : expectedActualValues.entrySet()) {
//			Integer expected = entry.getKey();
//			Integer actual = entry.getValue();
//			double delta = expected * deltaPercent / 100;
//			assertEquals(expected, actual, delta);
//		}
//	}
//
//	private ManagedSymmetricEssDummy createSymmetricEss(int expectedActivePower, int expectedReactivePower,
//			double deltaPercent) {
//		return new ManagedSymmetricEssDummy() {
//			@Override
//			public void applyPower(int activePower, int reactivePower) {
//				checkValues(expectedActivePower, expectedReactivePower, activePower, reactivePower, deltaPercent);
//			}
//		};
//	}
//
//	private ManagedAsymmetricEssDummy createAsymmetricEss(List<Integer> activePower, List<Integer> reactivePower,
//			double deltaPercent) {
//		return new ManagedAsymmetricEssDummy() {
//
//			@Override
//			public void applyPower(int activePowerL1, int reactivePowerL1, int activePowerL2, int reactivePowerL2,
//					int activePowerL3, int reactivePowerL3) {
//				Map<Integer, Integer> valuesToCheck = new HashMap<>();
//				valuesToCheck.put(activePower.get(0), activePowerL1);
//				valuesToCheck.put(activePower.get(1), activePowerL2);
//				valuesToCheck.put(activePower.get(2), activePowerL3);
//
//				valuesToCheck.put(reactivePower.get(0), reactivePowerL1);
//				valuesToCheck.put(reactivePower.get(1), reactivePowerL2);
//				valuesToCheck.put(reactivePower.get(2), reactivePowerL3);
//
//				checkValues(valuesToCheck, deltaPercent);
//			}
//
//		};
//	}
//
//	@Test
//	public void testSetActivePowerConstraintTypeRelationshipInt() {
//		sut = new Power(new ManagedSymmetricEssDummy());
//		int activePower = 1000;
//
//		int value = activePower;
//		Relationship relationship = Relationship.LEQ;
//		int[] indices = { 0 };
//		int noOfCoefficients = 2;
//		CoefficientOneConstraint expectedConstraint = new CoefficientOneConstraint(noOfCoefficients, indices,
//				relationship, value, "Expected Constraint");
//		LinearConstraint[] expected = expectedConstraint.getConstraints();
//
//		ConstraintType constraintType = ConstraintType.STATIC;
//		try {
//			CoefficientOneConstraint actualConstraint = sut.setActivePowerAndSolve(constraintType, relationship,
//					activePower);
//			LinearConstraint[] actual = actualConstraint.getConstraints();
//
//			assertEquals(expected.length, actual.length);
//			for (int i = 0; i < expected.length; i++) {
//				assertEquals(expected[i], actual[i]);
//			}
//
//		} catch (PowerException e) {
//			fail(e.getMessage());
//		}
//	}
}
