# AssDiary

"Ассоциативный дневник AssDiary" предназначен для статистического анализа текстов. Он создает список использованных слов (предварительно лемматизируя их) и выполняет следующие функции:
* Подсчитывает количество использований данного ключевого слова во всех обработанных текстах (игнорируя словоформы)
* Составляет для каждого ключевого слова список слов, использованных на определенной дистанции от данного ключевого слова (фактор близости)
* Составляет частотный список для каждой ассоциации, предположенной на факторе близости
* Сохраняет и загружает результаты в файлы
* Экспортирует результаты в формат для работы в утилите AssFinder

Обрабатывает сравнительно небольшие тексты.

Для работы необходима библиотека [mystem-scala](https://github.com/alexeyev/mystem-scala).
