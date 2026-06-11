import java.util.ArrayList;

public class User {
    protected String name;
    protected String type;
    protected int mood;
    protected int patienceThreshold;
    protected boolean isOnline;
    protected ArrayList<User> friends;
    protected ArrayList<Post> myPosts;

    public User(String name, String type, int patienceThreshold) {
        this.name = name;
        this.type = type;
        this.mood = 70;
        this.patienceThreshold = patienceThreshold;
        this.isOnline = true;
        this.friends = new ArrayList<>();
        this.myPosts = new ArrayList<>();
    }

    public void addFriend(User user) {
        if (!friends.contains(user)) {
            friends.add(user);
        }
    }

    public Post createPost(String postType, int weight) {
        Post newPost = new Post(this.name, postType, weight);
        this.myPosts.add(newPost);
        return newPost;
    }

    public String reactToPost(Post post, NetworkSystem network) {
        return "Пользователь " + this.name + " (" + this.type + ") прочитал пост от " + post.getAuthorName();
    }

    public void changeMood(int amount) {
        this.mood += amount;
        if (this.mood > 100) this.mood = 100;
        if (this.mood < 0) this.mood = 0;

        if (this.mood <= patienceThreshold) {
            this.isOnline = false;
        }
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public int getMood() { return mood; }
    public boolean isOnline() { return isOnline; }
    public ArrayList<User> getFriends() { return friends; }
    public ArrayList<Post> getMyPosts() { return myPosts; }

    public void reset() {
        this.mood = 70;
        this.isOnline = true;
        this.myPosts.clear();
    }
}
