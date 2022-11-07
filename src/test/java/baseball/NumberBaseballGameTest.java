package baseball;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

public class NumberBaseballGameTest {

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final PrintStream standardOut = System.out;

    @BeforeEach
    void setUpOut() {
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    void 게임_시작_문구를_출력한다() {
        BaseballGame baseballGame = new BaseballGame(new Hitter(), new Pitcher(), new Referee());
        String result = "숫자 야구 게임을 시작합니다.";

        baseballGame.playGame(false);

        assertThat(outputStream.toString().trim()).isEqualTo(result);
    }

    @Test
    void 특정_숫자와_순서를_가진_공을_하나_생성한다() {
        Ball ball = new Ball(1, 0);

        assertThat(ball).isInstanceOf(Ball.class);
    }

    @Test
    void 특정_숫자와_순서를_가진_공에서_숫자를_체크() throws NoSuchFieldException, IllegalAccessException {
        Ball ball = new Ball(3, 0);
        int result = 3;

        Field field = ball.getClass().getDeclaredField("number");
        field.setAccessible(true);
        int number = field.getInt(ball);

        assertThat(number).isEqualTo(result);
    }

    @Test
    void 특정_숫자와_순서를_가진_꽁에서_순서를_체크() throws NoSuchFieldException, IllegalAccessException {
        Ball ball = new Ball(1, 2);
        int result = 2;

        Field field = ball.getClass().getDeclaredField("order");
        field.setAccessible(true);
        int order = field.getInt(ball);

        assertThat(order).isEqualTo(result);
    }

    @Test
    void 랜덤한_1과_9사이의_숫자를_하나_생성한다() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Pitcher pitcher = new Pitcher();

        Method method = pitcher.getClass().getDeclaredMethod("getNotDuplicatedRandomNumber");
        method.setAccessible(true);
        int number = (int) method.invoke(pitcher);

        assertThat(number).isGreaterThan(0).isLessThan(10);
    }

    @Test
    void 투수가_랜덤한_1과_9사이의_숫자와_순서를_가진_공을_하나_생성한다() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Pitcher pitcher = new Pitcher();

        Method method = pitcher.getClass().getDeclaredMethod("throwRandomBall", int.class);
        method.setAccessible(true);
        Object ball = method.invoke(pitcher, 3);

        assertThat(ball).isInstanceOf(Ball.class);
    }

    @Test
    void 투수가_생성한_공에_1과_9사이의_숫자가_있는지_확인() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Pitcher pitcher = new Pitcher();

        Method method = pitcher.getClass().getDeclaredMethod("throwRandomBall", int.class);
        method.setAccessible(true);
        Ball ball = (Ball) method.invoke(pitcher, 3);

        assertThat(ball.getNumber()).isInstanceOf(Integer.class).isGreaterThan(0).isLessThan(10);
    }

    @Test
    void 서로_다른_1과_9사이의_랜덤_숫자를_가진_공_3개를_생성한다() {
        Pitcher pitcher = new Pitcher();

        List<Ball> balls = pitcher.throwRandomBalls(3);

        assertThat(balls.size()).isEqualTo(3);
    }

    @Test
    void 투수가_생성한_공_3개의_숫자가_모두_다른지_확인() {
        Pitcher pitcher = new Pitcher();
        List<Integer> checkList = new ArrayList<>(List.of(1, 0, 0, 0, 0, 0, 0, 0, 0, 0));

        List<Ball> balls = pitcher.throwRandomBalls(3);

        balls.stream()
                .peek(ball -> System.out.println(ball.getNumber()))
                .forEach(ball -> {
                    assertThat(checkList.get(ball.getNumber())).isEqualTo(0);
                    checkList.set(ball.getNumber(), 1);
                });
    }

    @Test
    void 입력된_수에서_특정_숫자의_개수를_가져온다() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BaseballGame baseballGame = new BaseballGame(new Hitter(), new Pitcher(), new Referee());
        int result = 1;

        Method method = baseballGame.getClass().getDeclaredMethod("getDigitCount", String.class, int.class);
        method.setAccessible(true);
        long digitCount = (long) method.invoke(baseballGame, "123", 3);

        assertThat(digitCount).isEqualTo(result);
    }

    @Test
    void 입력_길이가_3이_아니면_예외가_발생한다() throws NoSuchMethodException, IllegalAccessException {
        BaseballGame baseballGame = new BaseballGame(new Hitter(), new Pitcher(), new Referee());

        Method method = baseballGame.getClass().getDeclaredMethod("validateNumberLength", String.class);
        method.setAccessible(true);

        assertThatThrownBy(() -> method.invoke(baseballGame, "1234"))
                .isInstanceOf(InvocationTargetException.class);
    }

    @Test
    void 입력_값이_1과_9사이의_숫자가_아니면_예외가_발생한다() throws NoSuchMethodException, IllegalAccessException {
        BaseballGame baseballGame = new BaseballGame(new Hitter(), new Pitcher(), new Referee());

        Method method = baseballGame.getClass().getDeclaredMethod("validateNumberRange", String.class);
        method.setAccessible(true);


        assertThatThrownBy(() -> method.invoke(baseballGame, "120"))
                .isInstanceOf(InvocationTargetException.class);
    }

    @Test
    void 중복된_숫자가_있으면_예외가_발생한다() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BaseballGame baseballGame = new BaseballGame(new Hitter(), new Pitcher(), new Referee());

        Method method = baseballGame.getClass().getDeclaredMethod("validateNumberDuplication", String.class);
        method.setAccessible(true);

        assertThatThrownBy(() -> method.invoke(baseballGame, "133"))
                .isInstanceOf(InvocationTargetException.class);
    }

    @Test
    void 중복된_숫자가_없는_1과_9사이의_3자리_숫자면_예외가_발생하지_않는다() throws NoSuchMethodException {
        BaseballGame baseballGame = new BaseballGame(new Hitter(), new Pitcher(), new Referee());

        Method method = baseballGame.getClass().getDeclaredMethod("validate", String.class);
        method.setAccessible(true);

        assertThatNoException().isThrownBy(() ->
                method.invoke(baseballGame, "123"));
    }

    @Test
    void 예외가_발생하지_않으면_입력된_값을_반환한다() {
        BaseballGame baseballGame = new BaseballGame(new Hitter(), new Pitcher(), new Referee());
        String input = "234";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        String number = baseballGame.inputNumber();

        assertThat(number).isEqualTo(input);
    }

    @Test
    void 타자가_숫자를_받으면_해당_숫자의_공들을_생성한다() {
        Hitter hitter = new Hitter();
        List<Integer> result = List.of(1, 2, 3);

        List<Ball> balls = hitter.hitBalls("123");

        IntStream.range(0, balls.size())
                .forEach(index -> assertThat(balls.get(index).getNumber()).isEqualTo(result.get(index)));
    }

    @Test
    void 스트라이크가_있으면_true를_반환한다() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Ball ball = new Ball(3, 0);
        List<Ball> balls = List.of(new Ball(3, 0), new Ball(5, 1), new Ball(6, 2));

        Method method = ball.getClass().getDeclaredMethod("isStrike", List.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(ball, balls);

        assertThat(result).isEqualTo(true);
    }

    @Test
    void 스트라이크가_없으면_false를_반환한다() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Ball ball = new Ball(3, 0);
        List<Ball> balls = List.of(new Ball(2, 0), new Ball(3, 1), new Ball(6, 2));

        Method method = ball.getClass().getDeclaredMethod("isStrike", List.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(ball, balls);

        assertThat(result).isEqualTo(false);
    }

    @Test
    void 볼이_있으면_true를_반환한다() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Ball ball = new Ball(3, 0);
        List<Ball> balls = List.of(new Ball(2, 0), new Ball(3, 1), new Ball(6, 2));

        Method method = ball.getClass().getDeclaredMethod("isBall", List.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(ball, balls);

        assertThat(result).isEqualTo(true);
    }

    @Test
    void 볼이_없으면_false를_반환한다() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Ball ball = new Ball(3, 0);
        List<Ball> balls = List.of(new Ball(2, 0), new Ball(4, 1), new Ball(6, 2));

        Method method = ball.getClass().getDeclaredMethod("isBall", List.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(ball, balls);

        assertThat(result).isEqualTo(false);
    }

    @Test
    void 스트라이크면_스트라이크_결과를_반환한다() {
        Ball ball = new Ball(3, 0);
        List<Ball> balls = List.of(new Ball(3, 0), new Ball(5, 1), new Ball(6, 2));
        BaseballJudge result = BaseballJudge.STRIKE;

        BaseballJudge baseballJudge = ball.getResult(balls);

        assertThat(baseballJudge).isEqualTo(result);
    }

    @Test
    void 볼이면_볼_결과를_반환한다() {
        Ball ball = new Ball(3, 0);
        List<Ball> balls = List.of(new Ball(2, 0), new Ball(3, 1), new Ball(6, 2));
        BaseballJudge result = BaseballJudge.BALL;

        BaseballJudge baseballJudge = ball.getResult(balls);

        assertThat(baseballJudge).isEqualTo(result);
    }

    @Test
    void 아무것도_아니면_낫싱_결과를_반환한다() {
        Ball ball = new Ball(3, 0);
        List<Ball> balls = List.of(new Ball(2, 0), new Ball(4, 1), new Ball(6, 2));
        BaseballJudge result = BaseballJudge.NOTHING;

        BaseballJudge baseballJudge = ball.getResult(balls);

        assertThat(baseballJudge).isEqualTo(result);
    }

    @Test
    void 스트라이크면_스트라이크_카운트_하나를_올린다() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Referee referee = new Referee();

        Method method = referee.getClass().getDeclaredMethod("count", BaseballJudge.class);
        method.setAccessible(true);
        method.invoke(referee, BaseballJudge.STRIKE);

        assertThat(referee.getStrikeCount()).isEqualTo(1);
    }

    @Test
    void 볼이면_볼_카운트_하나를_올린다() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Referee referee = new Referee();

        Method method = referee.getClass().getDeclaredMethod("count", BaseballJudge.class);
        method.setAccessible(true);
        method.invoke(referee, BaseballJudge.BALL);

        assertThat(referee.getBallCount()).isEqualTo(1);
    }

    @Test
    void 게임결과를_판단한다_1스트라이크_1볼() {
        Referee referee = new Referee();
        List<Ball> hitterBalls = List.of(new Ball(2, 0), new Ball(3, 1), new Ball(6, 2));
        List<Ball> pitcherBalls = List.of(new Ball(2, 0), new Ball(4, 1), new Ball(3, 2));
        int resultStrikeCount = 1;
        int resultBallCount = 1;

        referee.judgeGameResult(hitterBalls, pitcherBalls);

        assertThat(referee.getStrikeCount()).isEqualTo(resultStrikeCount);
        assertThat(referee.getBallCount()).isEqualTo(resultBallCount);
    }

    @Test
    void 게임결과를_판단한다_2볼() {
        Referee referee = new Referee();
        List<Ball> hitterBalls = List.of(new Ball(2, 0), new Ball(3, 1), new Ball(6, 2));
        List<Ball> pitcherBalls = List.of(new Ball(3, 0), new Ball(2, 1), new Ball(4, 2));
        int resultStrikeCount = 0;
        int resultBallCount = 2;

        referee.judgeGameResult(hitterBalls, pitcherBalls);

        assertThat(referee.getStrikeCount()).isEqualTo(resultStrikeCount);
        assertThat(referee.getBallCount()).isEqualTo(resultBallCount);
    }

    @Test
    void 게임결과를_판단한다_3스트라이크() {
        Referee referee = new Referee();
        List<Ball> hitterBalls = List.of(new Ball(2, 0), new Ball(3, 1), new Ball(6, 2));
        List<Ball> pitcherBalls = List.of(new Ball(2, 0), new Ball(3, 1), new Ball(6, 2));
        int resultStrikeCount = 3;
        int resultBallCount = 0;

        referee.judgeGameResult(hitterBalls, pitcherBalls);

        assertThat(referee.getStrikeCount()).isEqualTo(resultStrikeCount);
        assertThat(referee.getBallCount()).isEqualTo(resultBallCount);
    }

    @Test
    void 볼_카운트가_2이면_2볼을_출력한다() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Referee referee = new Referee();
        List<Ball> hitterBalls = List.of(new Ball(2, 0), new Ball(3, 1), new Ball(6, 2));
        List<Ball> pitcherBalls = List.of(new Ball(3, 0), new Ball(2, 1), new Ball(4, 2));
        referee.judgeGameResult(hitterBalls, pitcherBalls);

        BaseballGame baseballGame = new BaseballGame(new Hitter(), new Pitcher(), referee);
        Method method = baseballGame.getClass().getDeclaredMethod("printResult");
        method.setAccessible(true);
        String result = "2볼";

        method.invoke(baseballGame);

        assertThat(outputStream.toString().trim()).isEqualTo(result);
    }

    @Test
    void 스트라이크_카운트가_1이면_1스트라이크를_출력한다() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Referee referee = new Referee();
        List<Ball> hitterBalls = List.of(new Ball(2, 0), new Ball(3, 1), new Ball(6, 2));
        List<Ball> pitcherBalls = List.of(new Ball(2, 0), new Ball(4, 1), new Ball(5, 2));

        BaseballGame baseballGame = new BaseballGame(new Hitter(), new Pitcher(), referee);
        Method method = baseballGame.getClass().getDeclaredMethod("printResult");
        method.setAccessible(true);
        String result = "1스트라이크";

        referee.judgeGameResult(hitterBalls, pitcherBalls);
        method.invoke(baseballGame);

        assertThat(outputStream.toString().trim()).isEqualTo(result);
    }

    @Test
    void 볼_카운트가_2_스트라이크_카운트가_1이면_2볼_1스트라이크를_출력한다() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Referee referee = new Referee();
        List<Ball> hitterBalls = List.of(new Ball(2, 0), new Ball(5, 1), new Ball(4, 2));
        List<Ball> pitcherBalls = List.of(new Ball(2, 0), new Ball(4, 1), new Ball(5, 2));
        referee.judgeGameResult(hitterBalls, pitcherBalls);

        BaseballGame baseballGame = new BaseballGame(new Hitter(), new Pitcher(), referee);
        Method method = baseballGame.getClass().getDeclaredMethod("printResult");
        method.setAccessible(true);
        String result = "2볼 1스트라이크";

        method.invoke(baseballGame);

        assertThat(outputStream.toString().trim()).isEqualTo(result);
    }

    @Test
    void 아무것도_없으면_낫싱을_출력한다() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Referee referee = new Referee();
        List<Ball> hitterBalls = List.of(new Ball(5, 0), new Ball(6, 1), new Ball(7, 2));
        List<Ball> pitcherBalls = List.of(new Ball(2, 0), new Ball(3, 1), new Ball(4, 2));
        referee.judgeGameResult(hitterBalls, pitcherBalls);
        BaseballGame baseballGame = new BaseballGame(new Hitter(), new Pitcher(), referee);

        Method method = baseballGame.getClass().getDeclaredMethod("printResult");
        method.setAccessible(true);
        String result = "낫싱";

        method.invoke(baseballGame);

        assertThat(outputStream.toString().trim()).isEqualTo(result);
    }

    @Test
    void 스트라이크_카운트가_3이면_게임이_종료된다() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Referee referee = new Referee();
        List<Ball> hitterBalls = List.of(new Ball(2, 0), new Ball(3, 1), new Ball(6, 2));
        List<Ball> pitcherBalls = List.of(new Ball(2, 0), new Ball(3, 1), new Ball(6, 2));
        referee.judgeGameResult(hitterBalls, pitcherBalls);

        BaseballGame baseballGame = new BaseballGame(new Hitter(), new Pitcher(), referee);
        Method method = baseballGame.getClass().getDeclaredMethod("isGameOver");
        method.setAccessible(true);

        boolean isGameOver = (boolean) method.invoke(baseballGame);

        assertThat(isGameOver).isEqualTo(true);
    }

    @Test
    void 스트라이크_카운트가_3이하면_게임이_종료되지_않는다() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Referee referee = new Referee();
        List<Ball> hitterBalls = List.of(new Ball(2, 0), new Ball(3, 1), new Ball(6, 2));
        List<Ball> pitcherBalls = List.of(new Ball(1, 0), new Ball(3, 1), new Ball(6, 2));
        referee.judgeGameResult(hitterBalls, pitcherBalls);

        BaseballGame baseballGame = new BaseballGame(new Hitter(), new Pitcher(), referee);
        Method method = baseballGame.getClass().getDeclaredMethod("isGameOver");
        method.setAccessible(true);

        boolean isGameOver = (boolean) method.invoke(baseballGame);

        assertThat(isGameOver).isEqualTo(false);
    }
}
