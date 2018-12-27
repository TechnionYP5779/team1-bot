
src_filename = 'Technion - Video Server Courses List.html'
dst_filename = 'old_server_videos.csv'

file_in = open(src_filename, 'r')
file_out = open(dst_filename, 'w')

file_out.write("ID, filimingDate, videoType, link\n")

for line in file_in:
    ID = 'NULL'
    date = 'NULL'
    videotype = 'NULL'
    link = 'NULL'

    ID = (line[len("<td>"):line.find('</td>', len("<td>"))])

    if 'Lecture</font>' in line:
        videotype = '\'Lecture\''

    if 'Tutorial</font>' in line:
        videotype = '\'Tutorial\''

    if 'Workshop</font>' in line:
        videotype = '\'Workshop\''

    if 'Appendix</font>' in line:
        videotype = '\'Appendix\''

    if 'QA</font>' in line:
        videotype = '\'QA\''

    link = '\'' + (line[line.find('https://video.technion.ac.il') : line.find('\"><nobr>')]) + '\''

    file_out.write(','.join([ID, date, videotype, link]) + "\n")

file_in.close()
file_out.close()