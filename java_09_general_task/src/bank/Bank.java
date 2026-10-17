package bank;

public class Bank {

    /**
     * Moves money between two accounts as one indivisible step.
     *
     * @return true if the transfer was made, false if it was rejected
     *         (same account, non-positive amount or insufficient funds)
     */
    public boolean transfer(Account from, Account to, int amount) {
        if (from == to || amount <= 0) {
            return false;
        }

        // Always lock the account with the smaller id first. If two threads
        // moved money in opposite directions (A->B and B->A) and each locked
        // "its own" source account first, they would wait for each other forever.
        Account first = from.getId() < to.getId() ? from : to;
        Account second = first == from ? to : from;

        synchronized (first) {
            synchronized (second) {
                if (from.getBalance() < amount) {
                    return false;
                }
                from.withdraw(amount);
                to.deposit(amount);
                return true;
            }
        }
    }
}