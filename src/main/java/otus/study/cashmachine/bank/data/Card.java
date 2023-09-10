package otus.study.cashmachine.bank.data;

public class Card {
    long id;

    private Long accountId;
    private String number;
    private String pinCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        if (id != card.id) return false;
        if (accountId != null ? !accountId.equals(card.accountId) : card.accountId != null) return false;
        if (number != null ? !number.equals(card.number) : card.number != null) return false;
        return pinCode != null ? pinCode.equals(card.pinCode) : card.pinCode == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (pinCode != null ? pinCode.hashCode() : 0);
        return result;
    }

    public Card(final long id, final String number, final Long accountId, final String pinCode) {
        this.id = id;
        this.number = number;
        this.accountId = accountId;
        this.pinCode = pinCode;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(final Long accountId) {
        this.accountId = accountId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(final String pinCode) {
        this.pinCode = pinCode;
    }
}
