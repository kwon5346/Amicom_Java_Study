import java.util.Scanner;
public class cardGame{
    static Card[] deck; // 배열
    static Player[] player;
    static Hand[] people;
    static int count = 52; // deck 에 남은 카드의 수
    static int broke = 0;

    public static void main(String[] args)
    {
        // 게임 시작 //
        System.out.println("Game Started....!");
        System.out.print("Enter the number of players: ");
        Scanner scanner = new Scanner(System.in);
        int N;
        // N 입력 받기 //
        N = scanner.nextInt();

        // 버퍼 정리 //
        scanner.nextLine();

        // 플레이어 배열 //
        player = new Player[N];

        // 내기 결과 값을 넣을 배열 선언 //
        int [] answer = new int[N];

        // 딜러의 이름과 돈 //
        Player host;
        host = new Player();
        host.name = "dealer";
        host.game_money = 0;

        // 참가자가 받는 두카드 클래스의 배열 //
        people = new Hand[N];

        // 참가자 이름과 돈 입력 받기 //
        for(int x = 0; x < N; x++){
            player[x] = new Player();
            System.out.print("Enter the player-"+ (x + 1) + "(name, game money): ");
            String inputLine = scanner.nextLine();
            String[] parts = inputLine.split(" ");
            if(parts.length == 2){
                player[x].name = parts[0];
                player[x].game_money = Integer.parseInt(parts[1]);
            }
            else{
                System.out.println("Wrong Input. Start again");
                break;
            }
        }

        // 배팅 금액 입력 받기 //
        int betting_amount;
        System.out.print("Enter the betting amount: ");
        betting_amount = scanner.nextInt();

        // 버퍼 지우기 //
        scanner.nextLine();

        // 카드 덱 생성 //
        initialize();

        // 게임 시작 //
        do{


            // 참가자 중 돈 없는 사람 있으면 이름 출력하고  반복문 종료 //
            for(int x = 0; x < N; x++){
                if(player[x].game_money < betting_amount){
                    System.out.println(player[x].name + " has no enough game money!");
                    broke++; // 여러명 있을 수 있으니 바로 break 를 하지 않는다 //
                }
            }
            // 한 명 이상이면 반복문 종료 //
            if(broke > 0)
                break;

            // 딜러의 핸드 클래스 생성( 핸드 클래스는 카드 두장이 있는 클래스 ) //
            Hand dealer;
            dealer = new Hand();

            // 카드가 이제 없으면 반복문 종료 //
            if(count == 0)
                break;

            // 딜러에게 주어지는 카드 두개 중 하나 뽑음 //
            dealer.A = deal();

            // Ace 가 가장 커야 하니까 1을 입력 받으면 14로 취급 //
            if(dealer.A.rank == 1)
                dealer.A.rank = 14;

            // 카드 없으면 반복문 종료 //
            if(count == 0)
                break;

            // 딜러에세 주어지는 두번쨰 카드 //
            dealer.B = deal();
            if(dealer.B.rank == 1)
                dealer.B.rank = 14;

            // 반복문으로 N 만큼의 사람에게 카드를 줄건데 만약 그 와중에 카드가 다 떨어지는것을 알려주기 위한 변수 //
            int check = 0;

            // 반복문으로 N 만큼의 사람에게 카드 주기 //
            for(int y = 0; y < N; y++){

                // player 배열(Player 클래스)이 아닌 people 배열(Hand 클래스)로 받음 //
                people[y] = new Hand();

                // 카드 떨어지면 check 하나 증가하고 반복문 종료 //
                if(count == 0){
                    check = 1;
                    break;
                }

                // 첫번째 카드 주기 //
                people[y].A = deal();
                if(people[y].A.rank == 1)
                    people[y].A.rank = 14;

                // 카드 떨어졋나 체크 //
                if(count == 0){
                    check = 1;
                    break;
                }

                // 두번째 카드 주기 //
                people[y].B = deal();
                if(people[y].B.rank == 1)
                    people[y].B.rank = 14;
            }
            // 카드 다 떨어졌으면 이 반복문도 종료 //
            if(check == 1)
                break;

            // 각 사람들의 뽑은 두장의 카드를 출력해야 하는데, 큰게 앞으로 오도록 설정 //
            for(int x = 0; x < N; x++){
                int a = compare(people[x].A, people[x].B);

                // 카드 52장이 다 다르므로 같은 경우는 없음 //
                if(a == 1){
                    System.out.println(player[x].name + " : (" + toString(people[x].A) +", " + toString(people[x].B) + ")");
                }
                if(a == -1){
                    System.out.println(player[x].name + " : (" + toString(people[x].B) +", " + toString(people[x].A) + ")");
                }
            }

            // 딜러가 뽑은 두장의 카드 출력하는데, 큰게 앞에 오게 //
            int a = compare(dealer.A, dealer.B);

            // 비길수는 없음 //
            if(a == 1){
                System.out.println("Dealer : (" + toString(dealer.A) + ", " + toString(dealer.B) + ")");
            }
            if(a == -1){
                System.out.println("Dealer : (" + toString(dealer.B) + ", " + toString(dealer.A) + ")");
            }

            // N명의 사람들과 딜러간의 내기 결과를 answer 배열에 저장 //
            for(int x = 0; x < N; x++){
                answer[x] = hand_compare(people[x], dealer);
            }

            // 내기 결과 출력하기 //
            for(int x = 0; x < N; x++){

                String s;

                // 비겼으면 해당 사람의 tie_count 올려줌 //
                if(answer[x] == 0){
                    player[x].tie_count++;
                    System.out.println(player[x].name + " tied " + " ($" + player[x].game_money + ")");
                }

                // 이겼으면 해당 사람의 win_count 를 올려줌, 돈도 올려줌 //
                else if(answer[x] == 1){
                    s = "won";
                    player[x].win_count++;
                    player[x].game_money += betting_amount;
                    // 딜러에게서 뻇어옴. 근데 딜러는 0원으로 유지 //
                    host.game_money -= betting_amount;
                    host.game_money += betting_amount;
                    int temp = player[x]. game_money;
                    System.out.println(player[x].name +" " + s + " $" + betting_amount + " ($" + temp + ")");
                }

                // 졌으면 해당 사람의 lose_count 올려주고 돈도 빼줌 //
                else{
                    s = "lost";
                    player[x].lose_count++;
                    player[x].game_money -= betting_amount;
                    // 딜러에게 줌. 근데 딜러는 0원으로 유지하게 설정 //
                    host.game_money += betting_amount;
                    host.game_money -= betting_amount;
                    int temp = player[x]. game_money;
                    System.out.println(player[x].name + " " + s + " $" + betting_amount +  " ($" + temp + ")");
                }

            }

            // 대화 형식으로 계속 경기를 진행 할건지 질문 //
            String str;
            System.out.print("Continue? ");
            str = scanner.nextLine();

            // y면 계속, y가 아니면 반복문 종료 //
            if(str.equals("y")){
                continue;
            }
            else{
                break;
            }
        }while(true);

        // 카드가 없어서 끝났다면 아래 문구를 출력 //
        if(count <= 0){
            System.out.println("No more card!");
            System.out.println("Games Ended..");
        }

        // 그냥 끝났거나, 한 명이 돈이 없어서 끝났다면 아래 문구 출력 //
        else{
            System.out.println("Games Ended..");
        }

        // 전체 경기는 한 사람의 이긴 경기 + 진 경기 + 비긴 경기의 합이다 //
        System.out.println("Total games: " + (player[0].win_count + player[0].lose_count + player[0].tie_count));

        // 각 사람의 게임 결과 정보 출력 //
        for(int x = 0; x < N; x++){
            System.out.println(player[x].name + ": " + player[x].win_count + " wins, " +  player[x].lose_count + " loses, " + player[x].tie_count + "ties, $" + player[x].game_money);
        }
    } // main 끝

    // 카드 덱에 52장 넣기 //
    static void initialize(){
        deck = new Card[52];
        int a = 1;
        int b = 1;
        // 랭크는 13번씩 반복되도록, 슈트는 4번씩 반복되도록 //
        for(int x = 0; x < 52; x++){
            deck[x] = new Card();
            deck[x].rank = a;
            a++;
            if(a == 14)
                a = 1;
            deck[x].suit = suitInString(b);
            b++;
            if(b == 5)
                b = 1;
        }
    }

    // 랜덤으로 나눠주면서 카운트도 하나씩 빼기 //
    static Card deal(){

        if(count == 0){
            return null;
        }
        int list[] = new int[count];
        for(int x = 0; x < count; x++){
            list[x] = x;
        }
        int pick = (int)(Math.random() * count);
        Card A = deck[list[pick]];
        deck[list[pick]] = deck[count - 1];
        count -= 1;
        return A;
    }

    // 두 장씩 비교 //
    static int hand_compare(Hand h1, Hand h2){
        // 둘 다 원페어 //
        if(h1.A.rank == h1.B.rank){
            if(h2.A.rank == h2.B.rank){
                int a = compare(h1.A, h2.A);
                if(h1.A.rank > h2.A.rank)
                    return 1;
                else if(h1.A.rank == h2.A.rank)
                    return 0;
                else
                    return -1;
            }
            /// A만 원페어 ///
            else{
                return 1;
            }
        }
        // B만 원페어 //
        else if(h2.A.rank == h2.B.rank){
                return -1;
        }
        // 둘다 노페어 //
        else{
            // 큰 수 찾기 //
            int a_big, a_small, b_big, b_small;
            a_big = h1.A.rank;
            a_small = h1.B.rank;

            if(h1.B.rank >= a_big){
                a_big = h1.B.rank;
                a_small = h1.A.rank;
            }
            b_big = h2.A.rank;
            b_small = h2.B.rank;
            if(h2.B.rank > b_big){
                b_big = h2.B.rank;
                b_small = h2.A.rank;
            }
            // 둘다 노페어 인데 A가 더 크다 //
            if(a_big > b_big){
                return 1;
            }
            // 둘다 노페어 인데 rank 큰 값도 같은데 작은 값은 A가 크다 //
            else if(a_big == b_big){
                if(a_small > b_small)
                    return 1;
                // 둘다 노페어인데 제일 큰 값도 같고 작은 값도 같음 //
                else if(a_small == b_small){
                    if(a_big == h1.A.rank){
                        if(b_big == h2.A.rank){
                            int a = compare(h1.A, h2.A);
                            if(a == 0)
                                return 0;
                            else if(a==1)
                                return 1;
                            else return -1;
                        }
                        else{
                            int a = compare(h1.A, h2.B);
                            if(a == 0)
                                return 0;
                            else if(a==1)
                                return 1;
                            else return -1;
                        }
                    }
                    else{
                        if(b_big == h2.A.rank){
                            int a = compare(h1.B, h2.A);
                            if(a == 0)
                                return 0;
                            else if(a==1)
                                return 1;
                            else return -1;
                        }
                        else{
                            int a = compare(h1.B, h2.B);
                            if(a == 0)
                                return 0;
                            else if(a==1)
                                return 1;
                            else return -1;
                        }
                    }
                }
                // 둘다 노페어인데 제일 큰 값 같고 작은 값은 B가 크다 //
                else return -1;
            }
            // 둘다 노페어인데 큰 값은 B가 더 크다 //
            else return -1;
        }
    }

    // 카드 한장씩 비교 //
    static int compare(Card c1, Card c2)
    {
        if(c1.rank == 1){
            if(c2.rank == 1){
                return 0;
            }
            else{
                return 1;
            }
        }
        else if(c2.rank == 1){
            return -1;
        }
        else if(c1.rank > c2.rank){
            return 1;
        }
        else if(c2.rank > c1.rank){
            return -1;
        }
        else{
            if(c1.suit.equals("Spade")){
                if(c2.suit.equals("Spade")){
                    return 0;
                }
                else if(c2.suit.equals("Diamond")){
                    return 1;
                }
                else if(c2.suit.equals("Heart")){
                    return 1;
                }
                else {
                    return 1;
                }
            }
            else if(c1.suit.equals("Diamond")){
                if(c2.suit.equals("Spade")){
                    return -1;
                }
                else if(c2.suit.equals("Diamond")){
                    return 0;
                }
                else if(c2.suit.equals("Heart")){
                    return 1;
                }
                else {
                    return 1;
                }
            }
            else if(c1.suit.equals("Heart")){
                if(c2.suit.equals("Spade")){
                    return -1;
                }
                else if(c2.suit.equals("Diamond")){
                    return -1;
                }
                else if(c2.suit.equals("Heart")){
                    return 0;
                }
                else {
                    return 1;
                }
            }
            else {
                if(c2.suit.equals("Spade")){
                    return -1;
                }
                else if(c2.suit.equals("Diamond")){
                    return -1;
                }
                else if(c2.suit.equals("Heart")){
                    return -1;
                }
                else{
                    return 0;
                }
            }
        }
    }


    // 입력받은 int 를 String 으로 변환
    static String suitInString(int suit)
    {
        if(suit == 1){
            return "Spade";
        }

        else if(suit == 2){
            return "Diamond";
        }
        else if(suit == 3){
            return "Heart";
        }
        else if(suit == 4){
            return "Club";
        }
        else{
            return "Wrong input";
        }
    }
    static String toString(Card c)
    {
        if(c.rank == 14){
            String s;
            s = "Ace of " + c.suit;
            return s;
        }
        else if(c.rank == 11){
            String s;
            s = "Jack of " + c.suit;
            return s;
        }
        else if(c.rank == 12){
            String s;
            s = "Queen of " + c.suit;
            return s;
        }
        else if(c.rank == 13){
            String s;
            s = "King of " + c.suit;
            return s;
        }
        else{
            String s;
            s = c.rank + " of " + c.suit;
            return s;
        }

    }

} // end of class CardGame
class Card
{
    String suit;
    int rank;
}
class Player{
    String name;
    int game_money;
    int win_count;
    int lose_count;
    int tie_count;
}
class Hand{
    Card A;
    Card B;
}