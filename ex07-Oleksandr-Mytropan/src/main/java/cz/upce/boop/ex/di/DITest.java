package cz.upce.boop.ex.di;

public class DITest {

    public static class DepA {

        @Inject
        public DepA(DepB depb) {
            System.out.println("DepA " + depb);
        }

    }

    public static class DepB {

        @Inject
        public DepB() {
        }
    }

    public static class DepC {

        public DepC() {
        }
    }

    public static class DepD {

        @Inject
        public DepD(DepA depa, DepC depc) {
            System.out.println("DepD " + depa + " " + depc);
        }
    }

    public interface Int {

        void work();
    }

    public static class Crt implements Int {

        private DepA depa;
        private DepB depb;

        private DepC depc;

        @Inject
        private DepD depd;

        @Inject
        public Crt(DepA depa, DepB depb) {
            this.depa = depa;
            this.depb = depb;

            System.out.println("constructor " + depa + " " + depb);
        }

        @Inject
        public void method(DepC depc) {
            this.depc = depc;

            System.out.println("method " + depc);
        }

        @Init
        public void initMethod() {
            System.out.println("init");
        }

        @Destroy
        public void destroyMethod() {
            System.out.println("destroy");
        }

        @Override
        public void work() {
            System.out.println("work " + depd);
        }

    }

    public static void main(String[] args) {
        DIContainer container = new DIContainer();
        container.register(DepA.class, DepA.class, Scope.TRANSIENT);
        container.register(DepB.class, DepB.class, Scope.TRANSIENT);
        container.register(DepC.class, DepC.class, Scope.TRANSIENT);
        container.register(DepD.class, DepD.class, Scope.TRANSIENT);
        container.register(Int.class, Crt.class, Scope.SINGLETON);

        Int i = container.getInstance(Int.class);

        System.out.println(i);
        i.work();

        container.shutdown();

    }

//DepA cz.upce.boop.ex.di.DITest$DepB@6f539caf
//constructor cz.upce.boop.ex.di.DITest$DepA@1f17ae12cz.upce.boop.ex.di.DITest$DepB@4d405ef7
//DepA cz.upce.boop.ex.di.DITest$DepB@2e817b38
//DepD cz.upce.boop.ex.di.DITest$DepA@433c675d cz.upce.boop.ex.di.DITest$DepC@3f91beef
//method cz.upce.boop.ex.di.DITest$DepC@179d3b25
//init
//cz.upce.boop.ex.di.DITest$Crt@20ad9418
//work cz.upce.boop.ex.di.DITest$DepD@31cefde0
//destroy
}
