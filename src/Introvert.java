public class Introvert extends User {

    public Introvert(String name, int patienceThreshold) {
        super(name, "Интроверт", patienceThreshold);
        this.mood = 50;
    }

    @Override
    public String reactToPost(Post post, NetworkSystem network) {
        for (Comment c : post.getComments()) {
            if (c.getToxicity() < 0) {
                this.changeMood(-5);
                return "Интроверт " + this.name + " увидел токсичность в комментариях и расстроился (Настроение: " + this.mood + "%)";
            }
        }
        return "Интроверт " + this.name + " спокойно изучил публикацию";
    }
}
