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

data=load('C:\workspace\projects\eclipse\MultiSwitchCM\data\targetFlowNum_diffVolume_vs_Overhead\diffTargetFlowNumChangeVolume_VS_Overhead.txt');
figure
hold on;
scatter(data(:,1), data(:,5)*4*6)
scatter(data(:,1), data(:,4)*8)
scatter(data(:,1), data(:,4)*8+data(:,5)*4*6)

set(gca, 'FontSize', 18);
legend('host=>switches', 'switches=>controller', 'sum');
legend('Location', 'northwest');
xlabel('number of target flows(different volume)')
ylabel('Network overhead')
hold off;

data=load('C:\workspace\projects\eclipse\MultiSwitchCM\data\targetFlowNum_diffLossRate_vs_Overhead\diffTargetFlowNumChangeLossRate_VS_Overhead.txt');
figure
hold on;
scatter(data(:,1), data(:,5)*4*6)
scatter(data(:,1), data(:,4)*8)
scatter(data(:,1), data(:,4)*8+data(:,5)*4*6)

set(gca, 'FontSize', 18);
legend('host=>switches', 'switches=>controller', 'sum');
legend('Location', 'northwest');
xlabel('number of target flows(different loss rate)')
ylabel('Network overhead')
hold off;
end

