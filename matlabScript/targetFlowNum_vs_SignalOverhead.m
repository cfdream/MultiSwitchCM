function targetFlowNum_vs_SignalOverhead()
data=load('C:\workspace\projects\eclipse\MultiSwitchCM\data\targetFlowNum vs Overhead\multi_targetFlowNum_vs_Overhead.txt');
figure
hold on;
scatter(data(:,1), data(:,5)*4*6)
scatter(data(:,1), data(:,4)*8)
scatter(data(:,1), data(:,4)*8+data(:,5)*4*6)

set(gca, 'FontSize', 18);
legend('host=>switches', 'switches=>controller', 'sum');
legend('Location', 'northwest');
xlabel('number of target flows')
ylabel('Network overhead')
hold off;

end

