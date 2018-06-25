package util;

public class StringEnumerator {

    private String current;

    public StringEnumerator(){
        current = "A";
    }

    public String next(){
        String toReturn = current;
        StringBuilder s = new StringBuilder();

        boolean incr = true;
        for(int i = current.length() - 1; i >= 0; i--){
            char c = current.charAt(i);

            if(incr) {
                if (c == 'Z')
                    s.append('A');
                else {
                    s.append((char)(c + 1));
                    incr = false;
                }
            }
            else
                s.append(c);
        }

        if(incr)
            s.append('A');


        s.reverse();
        current = s.toString();

        return toReturn;
    }
}
