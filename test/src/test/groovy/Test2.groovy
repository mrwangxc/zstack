import org.zstack.testlib.Flow

/**
 * Created by xing5 on 2017/2/14.
 */
class Test2 {
    private void testFlow4(Closure success, Closure<String> failure) {
        // for unexpected exceptions:
        // 1. exception in step() will cause the flow rollback
        // 2. exception in done() will cause the error() to be called
        // 3. exception in error() will cause onUnexpectedFailure(),
        // onUnexpectedFailure is optional and should be very simple to call a uplink callback(e.g. failure)
        // to report an error. Exception in onUnexpectedFailure will not be handled
        Flow.create {
            step { next ->
                println("[testFlow4]step 1")
                next()
            }.onError { rollback ->
                println("[testFlow4]rollback step 1")
                rollback()
            }

            step { next ->
                throw new Exception("on purpose")
            }

            done {
                success()
            }

            error {
                throw new Exception("error in error() handler")
            }

            onUnexpectedFailure {
                failure(it as String)
            }
        }.run()
    }

    private void testFlow3(Closure success, Closure<String> failure) {
        // exception in step() causes flow rollback
        Flow.create {
            step { next ->
                println("[testFlow3]step 1")
                next()
            }.onError { rollback ->
                println("[testFlow3]rollback step 1")
                rollback()
            }

            step { next ->
                throw new Exception("on purpose")
            }

            done {
                success()
            }

            error {
                failure(it as String)
            }

        }.run()
    }

    private void testFlow2(Closure success, Closure<String> failure) {
        // call fail() to make a flow fails
        Flow.create {
            // the onError() is for a rollback function, which is optional
            step { next ->
                println("[testFlow2]step 1")
                next()
            }.onError { rollback ->
                println("[testFlow2]rollback step 1")
                rollback()
            }

            // the step receives two parameters that are functions
            // next: call it when the flow is done
            // fail: call it when you want to make the flow fail
            step { next, fail ->
                fail("[testFlow2]on purpose")
            }.onError { rollback ->
                // the rollback is the only parameter which is called to
                // notify that this flow is fully rolled back
                println("[testFlow1] rollback step2")
                rollback()
            }

            done {
                success()
            }

            error { String err ->
                failure(err)
            }

        }.run()
    }

    private void testFlow1(Closure success, Closure failure) {
        // all success
        Flow.create {
            step { next ->
                println("[testFlow1]step 1")
                next()
            }.onError { rollback ->
                println("[testFlow1]rollback step 1")
                rollback()
            }

            step { next ->
                println("[testFlow1]step 2")
                next()
            }

            done {
                success()
            }

            error {
                failure()
            }

        }.run()
    }

    private boolean randomBoolean() {
        return Math.random() < 0.5
    }

    // you can create sub flows
    private void complexFlow(Closure success, Closure failure) {
        Flow.create {
            step { next ->
                println("step 1.1")

                if (randomBoolean()) {
                    // create sub flow inline
                    subFlow {
                        step {
                            println("step A.1")
                            it()
                        }

                        done {
                            next()
                        }

                        error {
                            failure()
                        }
                    }.run()
                } else {
                    // for complex logic, it's better to create sub flows in a separate function
                    createAnotherFlow(next).run()
                }
            }

            done {
                success()
            }

            error {
                failure(it)
            }
        }.run()
    }

    def createAnotherFlow(Closure next) {
        return Flow.create {
            step {
                println("step B.1")
                it()
            }

            step {
                println("step B.2")
                it()
            }

            done { next() }
        }
    }

    //@org.junit.Test
    void test3() {
        testFlow3({
            println("[testFlow3]flow succeeds")
        }, {
            println("[testFlow3]flow fails, $it")
        })
    }

    //@org.junit.Test
    void test2() {
        testFlow2({
            println("[testFlow2]:flow succeeds")
        }, {
            println("[testFlow2]:flow fails, $it")
        })
    }

    //@org.junit.Test
    void test() {
        //new CreationSpecGenerator().generate("/root/zstack-testlib/CreationSpec.groovy")
        testFlow1({
            println("[testFlow1]flow succeeds")
        }, {
            println("[testFlow1]flow fails")
        })
    }

    //@org.junit.Test
    void test4() {
        testFlow4({
            println("[testFlow4]:flow succeeds")
        }, {
            println("[testFlow4]:flow fails, $it")
        })
    }

    @org.junit.Test
    void complexFlowTest() {
        complexFlow({
            println("complex flow done")
        }, {
            println("complex flow fails, $it")
        })
    }
}
