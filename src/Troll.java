public class Troll extends User {

    public Troll(String name, int patienceThreshold) {
        super(name, "Тролль", patienceThreshold);
    }

    @Override
    public String reactToPost(Post post, NetworkSystem network) {
        User victim = network.findUser(post.getAuthorName());

        if (victim != null && victim.isOnline() && !victim.getName().equals(this.name)) {
            int toxicity = -15;
            Comment trollComment = new Comment(this.name, this.type, "Негативный комментарий", toxicity);
            post.addComment(trollComment);
            victim.changeMood(toxicity);

            network.callSycophants(this, post);

            return "Пользователь " + this.name + " (" + this.type + ") оставил ехидный комментарий у " + victim.getName();
        }
        return "Пользователь " + this.name + " (" + this.type + ") проигнорировал публикацию";
    }
}
