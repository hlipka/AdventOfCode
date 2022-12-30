class Packet:
    def __init__(self, bits):
        self.packets = []
        self.value = -1

        self.version = self.bits_to_number(bits[:3])
        self.type = self.bits_to_number(bits[3:6])
        if self.type == 4:
            self.bit_length = self.parse_literal(bits[6:]) + 6
        else:
            self.bit_length = self.parse_operator(bits[6:]) + 6

    @staticmethod
    def bits_to_number(bits):
        num = 0
        pos = 1
        for b in reversed(bits):
            num += b * pos
            pos *= 2
        return num

    def parse_literal(self, bits):
        p_count = 0
        result = []
        while bits[p_count * 5] == 1:
            packet = bits[p_count * 5 + 1:p_count * 5 + 5]
            result.extend(packet)
            p_count += 1
        packet = bits[p_count * 5 + 1:p_count * 5 + 5]
        result.extend(packet)
        self.value = self.bits_to_number(result)
        return p_count * 5 + 5  # number of bits consumed

    def parse_operator(self, bits):
        if bits[0] == 1:
            length = self.bits_to_number(bits[1:12])
            parsed_bits = 0
            parsed_packets = 0
            while parsed_packets < length:
                p = Packet(bits[parsed_bits + 12:])
                self.packets.append(p)
                pb = p.bit_length
                parsed_bits += pb
                parsed_packets += 1
            return parsed_bits + 12
        else:
            length = self.bits_to_number(bits[1:16])
            parsed_bits = 0
            while parsed_bits < length:
                p = Packet(bits[parsed_bits + 16:])
                self.packets.append(p)
                pb = p.bit_length
                parsed_bits += pb
            return length + 16

    def sum_versions(self):
        result = self.version
        for p in self.packets:
            result += p.sum_versions()
        return result


def get_bits(c):
    if c == '0':
        return [0, 0, 0, 0]
    if c == '1':
        return [0, 0, 0, 1]
    if c == '2':
        return [0, 0, 1, 0]
    if c == '3':
        return [0, 0, 1, 1]
    if c == '4':
        return [0, 1, 0, 0]
    if c == '5':
        return [0, 1, 0, 1]
    if c == '6':
        return [0, 1, 1, 0]
    if c == '7':
        return [0, 1, 1, 1]
    if c == '8':
        return [1, 0, 0, 0]
    if c == '9':
        return [1, 0, 0, 1]
    if c == 'A':
        return [1, 0, 1, 0]
    if c == 'B':
        return [1, 0, 1, 1]
    if c == 'C':
        return [1, 1, 0, 0]
    if c == 'D':
        return [1, 1, 0, 1]
    if c == 'E':
        return [1, 1, 1, 0]
    if c == 'F':
        return [1, 1, 1, 1]
    return []


def parse_packet_line(line):
    bits = []
    for c in line:
        bits.extend(get_bits(c))
    p = Packet(bits)
    print("version_sum", p.sum_versions())


def run(f_name):
    fin = open(f_name)
    line = fin.readline()
    parse_packet_line(line.strip())


# test messages
# parse_packet_line("D2FE28")
# parse_packet_line("38006F45291200")
# parse_packet_line("EE00D40C823060")
# parse_packet_line("8A004A801A8002F478")
# parse_packet_line("620080001611562C8802118E34")
# parse_packet_line("C0015000016115A2E0802F182340")
# parse_packet_line("A0016C880162017C3686B18A3D4780")

if __name__ == '__main__':
    run('../data/day16.txt')
