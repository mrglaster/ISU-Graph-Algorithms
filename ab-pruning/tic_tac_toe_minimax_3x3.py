import pygame
import sys
from math import inf

pygame.init()

SCREEN_SIZE = 300
CELL_SIZE = SCREEN_SIZE // 3
LINE_COLOR = (0, 0, 0)
X_COLOR = (200, 0, 0)
O_COLOR = (0, 0, 200)
BG_COLOR = (255, 255, 255)
FONT = pygame.font.SysFont(None, 60)
BUTTON_FONT = pygame.font.SysFont(None, 40)

screen = pygame.display.set_mode((SCREEN_SIZE, SCREEN_SIZE + 80))
pygame.display.set_caption("Tic-Tac-Toe (AB Pruning, Minimax)")

board = [[0, 0, 0],
         [0, 0, 0],
         [0, 0, 0]]
game_over = False
winner_text = ""


def draw_board():
    screen.fill(BG_COLOR)
    for x in range(1, 3):
        pygame.draw.line(screen, LINE_COLOR, (0, x * CELL_SIZE), (SCREEN_SIZE, x * CELL_SIZE), 3)
        pygame.draw.line(screen, LINE_COLOR, (x * CELL_SIZE, 0), (x * CELL_SIZE, SCREEN_SIZE), 3)

    pygame.draw.line(screen, LINE_COLOR, (0, SCREEN_SIZE), (SCREEN_SIZE, SCREEN_SIZE), 3)

    for x in range(3):
        for y in range(3):
            if board[x][y] == 1:
                draw_x(x, y)
            elif board[x][y] == -1:
                draw_o(x, y)


def draw_x(row, col):
    x_start = col * CELL_SIZE
    y_start = row * CELL_SIZE
    pygame.draw.line(screen, X_COLOR, (x_start + 20, y_start + 20), (x_start + CELL_SIZE - 20, y_start + CELL_SIZE - 20), 5)
    pygame.draw.line(screen, X_COLOR, (x_start + 20, y_start + CELL_SIZE - 20), (x_start + CELL_SIZE - 20, y_start + 20), 5)


def draw_o(row, col):
    center = (col * CELL_SIZE + CELL_SIZE // 2, row * CELL_SIZE + CELL_SIZE // 2)
    pygame.draw.circle(screen, O_COLOR, center, CELL_SIZE // 2 - 20, 5)


def clear_board():
    for x in range(3):
        for y in range(3):
            board[x][y] = 0


def win_check(player):
    conditions = [[board[0][0], board[0][1], board[0][2]],
                  [board[1][0], board[1][1], board[1][2]],
                  [board[2][0], board[2][1], board[2][2]],
                  [board[0][0], board[1][0], board[2][0]],
                  [board[0][1], board[1][1], board[2][1]],
                  [board[0][2], board[1][2], board[2][2]],
                  [board[0][0], board[1][1], board[2][2]],
                  [board[0][2], board[1][1], board[2][0]]]
    return [player, player, player] in conditions


def get_blanks():
    return [(x, y) for x in range(3) for y in range(3) if board[x][y] == 0]


def is_board_full():
    return len(get_blanks()) == 0


def set_move(x, y, player):
    board[x][y] = player


def get_score():
    if win_check(1):
        return 10
    elif win_check(-1):
        return -10
    return 0


def ab_minimax(depth, alpha, beta, player):
    if depth == 0 or win_check(1) or win_check(-1):
        return None, get_score()
    best_move = None
    if player == 1:
        max_eval = -inf
        for x, y in get_blanks():
            board[x][y] = player
            _, eval = ab_minimax(depth - 1, alpha, beta, -player)
            board[x][y] = 0
            if eval > max_eval:
                max_eval = eval
                best_move = (x, y)
            alpha = max(alpha, eval)
            if beta <= alpha:
                break
        return best_move, max_eval
    else:
        min_eval = inf
        for x, y in get_blanks():
            board[x][y] = player
            _, eval = ab_minimax(depth - 1, alpha, beta, -player)
            board[x][y] = 0
            if eval < min_eval:
                min_eval = eval
                best_move = (x, y)
            beta = min(beta, eval)
            if beta <= alpha:
                break
        return best_move, min_eval


def computer_move():
    move, _ = ab_minimax(len(get_blanks()), -inf, inf, -1)
    if move:
        set_move(move[0], move[1], -1)


def player_move(row, col):
    if board[row][col] == 0:
        set_move(row, col, 1)
        return True
    return False


def check_game_over():
    global game_over, winner_text
    if win_check(1):
        winner_text = "Player X wins!"
        game_over = True
    elif win_check(-1):
        winner_text = "Player O wins!"
        game_over = True
    elif is_board_full():
        winner_text = "It's a draw!"
        game_over = True


def draw_replay_button():
    pygame.draw.rect(screen, (0, 128, 0), (100, SCREEN_SIZE + 30, 120, 40))
    text = BUTTON_FONT.render("Replay", True, (255, 255, 255))
    screen.blit(text, (115, SCREEN_SIZE + 35))


def draw_winner_text():
    text = BUTTON_FONT.render(winner_text, True, (0, 0, 0))
    screen.blit(text, (SCREEN_SIZE // 2 - text.get_width() // 2, SCREEN_SIZE + 5))


def handle_replay():
    global game_over, winner_text
    clear_board()
    game_over = False
    winner_text = ""


def main():
    player_turn = True
    while True:
        draw_board()
        if game_over:
            draw_winner_text()
            draw_replay_button()
        pygame.display.flip()

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
                sys.exit()
            elif event.type == pygame.MOUSEBUTTONDOWN:
                if game_over:
                    x, y = pygame.mouse.get_pos()
                    if 100 <= x <= 200 and SCREEN_SIZE + 30 <= y <= SCREEN_SIZE + 70:
                        handle_replay()
                        player_turn = True
                elif player_turn:
                    x, y = pygame.mouse.get_pos()
                    row, col = y // CELL_SIZE, x // CELL_SIZE
                    if row < 3 and player_move(row, col):
                        player_turn = False
                        check_game_over()

        if not player_turn and not game_over:
            pygame.time.wait(500)
            computer_move()
            check_game_over()
            player_turn = True

if __name__ == '__main__':
    main()