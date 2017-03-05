package apps.fiefhaus.mandelbrot;

/**
 * Created by fiefhaus on 29.01.2016.
 */
public class Complex {

    public double re;
    public double im;

    public Complex() {

        this.re = 0;
        this.im = 0;
    }

    public Complex(double re, double im) {

        this.re = re;
        this.im = im;
    }

    public String toString() {

        String complex = "";

        if (this.im > 0)
            complex = this.re + " + " + this.im + "*i";

        else if (this.im == 0)
            complex = String.valueOf(this.re);

        else if (this.im < 0)
            complex = this.re + " - " + Math.abs(this.im) + "*i";

        return complex;
    }

    public void print() {

        if (this.im > 0)
            System.out.println(this.re + " + " + this.im + "*i");

        else if (this.im == 0)
            System.out.println(this.re);

        else if (this.im < 0)
            System.out.println(this.re + " - " + Math.abs(this.im) + "*i");
    }

    public void add(Complex a) {

        this.re = re + a.re;
        this.im = im + a.im;
    }

    public static Complex add(Complex a, Complex b) {

        double c_re, c_im;

        c_re = a.re + b.re;
        c_im = a.im + b.im;

        return new Complex(c_re, c_im);
    }

    public void sub(Complex a) {

        this.re = re - a.re;
        this.im = im - a.im;
    }

    public static Complex sub(Complex a, Complex b) {

        double c_re, c_im;

        c_re = a.re - b.re;
        c_im = a.im - b.im;

        return new Complex(c_re, c_im);
    }

    public void mul(Complex a) {

        this.re = this.re*a.re - this.im*a.im;
        this.im = this.re*a.im + this.im*a.re;
    }

    public static Complex mul(Complex a, Complex b) {

        double c_re, c_im;

        c_re = a.re*b.re - a.im*b.im;
        c_im = a.re*b.im + a.im*b.re;

        return new Complex(c_re, c_im);
    }

    public void div(Complex a) {

        this.re = ( this.re*a.re + this.im*a.im ) / ( a.re*a.re + a.im*a.im );
        this.im = ( this.im*a.re - this.re*a.im ) / ( a.re*a.re + a.im*a.im );
    }

    public static Complex div(Complex a, Complex b) {

        double c_re, c_im;

        c_re = ( a.re*b.re + a.im*b.im ) / ( b.re*b.re + b.im*b.im );
        c_im = ( a.im*b.re - a.re*b.im ) / ( b.re*b.re + b.im*b.im );

        return new Complex(c_re, c_im);
    }
}
