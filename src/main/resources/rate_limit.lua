
-- Dùng chiến lược fixed window
--[[-- Đây là file lua để gửi cho redis để chống rate limiting, được thực hiện nguyên tử (atomic)
   nên không dù có bao nhiêu luồng ập tới thì cũng chỉ 1 luồng đc cho qua]]
-- KEYS[1]: key trong redis
-- ARGV[1]: thời gian sống (VD: 60 giây)
-- ARGV[2]: Số lần cho phép tối đa (VD: 5 lần)

--[[ KHi 1 request gọi vào 1 api nó sẽ check trong redis, redis check xem vượt quá số lần tối đa
  được gọi thì reject]]

-- lấy giá trị của key, nếu chưa có thì tự tạo rồi tăng gtrị lên 1
local current = redis.call('INCR', KEYS[1]);

-- nếu là lần call api đầu tiên thì set thời gian sống cho key đó
if current == 1 then
    redis.call('EXPIRE', KEYS[1], ARGV[1]);
end

-- nếu số lượt call api vượt quá tối đa thi trả về 0 (reject)
if current > tonumber(ARGV[2]) then
    return 0;
end

-- nếu mọi thứ ổn thì trả về 0 (cho qua)
return 1;

