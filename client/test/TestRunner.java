import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("=== ViaBook Test Runner ===");
        System.out.println("Running Unit and System Tests...\n");
        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        System.out.println("--- Running Unit Tests ---");
        runTestClass(launcher, listener, "test.UserTest");
        runTestClass(launcher, listener, "test.AppointmentTest");
        System.out.println("\n--- Running System Tests ---");
        printTestSummary(listener.getSummary());
    }
    private static void runTestClass(Launcher launcher, SummaryGeneratingListener listener, String className) {
        try {
            Class<?> testClass = Class.forName(className);
            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build();
            launcher.execute(request, listener);
            System.out.println("✅ " + className + " completed");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ " + className + " not found: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error running " + className + ": " + e.getMessage());
        }
    }
    private static void printTestSummary(TestExecutionSummary summary) {
        if (summary == null) {
            System.out.println("No tests were found or executed.");
            return;
        }
        System.out.println("\n=== Test Execution Summary ===");
        System.out.println("Tests run: " + summary.getTestsFoundCount());
        System.out.println("Tests succeeded: " + summary.getTestsSucceededCount());
        System.out.println("Tests failed: " + summary.getTestsFailedCount());
        System.out.println("Total time: " + (summary.getTimeFinished() - summary.getTimeStarted()) + " ms");
        if (summary.getTestsFailedCount() > 0) {
            System.out.println("\n=== Failed Tests ===");
            summary.getFailures().forEach(failure -> {
                System.out.println("- " + failure.getTestIdentifier().getDisplayName());
                System.out.println("  Error: " + failure.getException().getMessage());
            });
        }
        if (summary.getTestsSucceededCount() == summary.getTestsFoundCount()) {
            System.out.println("\n\ud83c\udf89 All tests passed successfully!");
        } else {
            System.out.println("\n\u26a0\ufe0f  Some tests failed. Please check the output above.");
        }
    }
} 