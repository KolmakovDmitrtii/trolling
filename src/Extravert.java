public class Extravert extends User {

    public Extravert(String name, int patienceThreshold) {
        super(name, "Экстраверт", patienceThreshold);
        this.mood = 85;
    }

    @Override
    public String reactToPost(Post post, NetworkSystem network) {
        boolean underAttack = false;
        for (Comment c : post.getComments()) {
            if (c.getToxicity() < 0) {
                underAttack = true;
                break;
            }
        }

        if (underAttack && !post.getAuthorName().equals(this.name)) {
            User victim = network.findUser(post.getAuthorName());
            if (victim != null && victim.isOnline()) {
                Comment supportComment = new Comment(this.name, this.type, "Поддерживающий комментарий", 10);
                post.addComment(supportComment);
                victim.changeMood(10);
                this.changeMood(5);
                return "Экстраверт " + this.name + " заступился за " + victim.getName() + " и поднял ему настроение!";
            }
        }
        return "Экстраверт " + this.name + " одобрил публикацию пользователя " + post.getAuthorName();
    }
}
