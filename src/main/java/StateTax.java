public class StateTax {
    String country;
    Integer standard_rate;
    String name;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getStandard_rate() {
        return standard_rate;
    }

    public void setStandard_rate(Integer standard_rate) {
        this.standard_rate = standard_rate;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
                   return "\b" + "\b" + name + " " + country + " "
                    + standard_rate +  " %" + "\n";

    }
}
