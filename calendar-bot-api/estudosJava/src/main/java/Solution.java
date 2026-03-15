public class Solution {

    public static <Person> void main(String[] args) {

        Person person;

        person.
    class Person{
        int age;

        public Person(int initialAge){
            if(initialAge < 0){
                System.out.println("Age is not valid, setting age to 0.");
                age = 0;
                return;
            }
        }
        public int yearPasses(){
            return age++;
        }
        public String amIOld(){
            if(age < 13){
                return "You are young.";
            } else if(age >= 13 && age < 18){
                return "You are a teenager.";
            } else {
                return "You are old.";
             }

    }}}}
