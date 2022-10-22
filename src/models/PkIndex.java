package models;

import exceptions.TDEException;

public class PkIndex {
    private Long id;
    private Long address;

    public PkIndex(String line) throws TDEException {
        String[] split = line.split(",");

        if (split.length != 2) throw new TDEException("Formato não identificado");

        this.id = Long.parseLong(split[0]);
        this.address = Long.parseLong(split[1]);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAddress() {
        return address;
    }

    public void setAddress(Long address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "PkIndex{" +
                "id=" + id +
                ", address=" + address +
                '}';
    }
}
