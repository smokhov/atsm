package json;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2018-01-24
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class Car {

    private String name;
    private String years;
    private String make;
    private String model;
    private String engine;

    @Override
    public String toString() {
        return "Car{" +
               "name='" + name + '\'' +
               ", years='" + years + '\'' +
               ", make='" + make + '\'' +
               ", model='" + model + '\'' +
               ", engine='" + engine + '\'' +
               '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYears() {
        return years;
    }

    public void setYears(String years) {
        this.years = years;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }
}
